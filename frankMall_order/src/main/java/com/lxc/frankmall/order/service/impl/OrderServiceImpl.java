package com.lxc.frankmall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxc.common.entity.MemberEntity;
import com.lxc.common.exception.NoStockException;
import com.lxc.common.to.OrderTo;
import com.lxc.common.to.mq.SeckillOrderTo;
import com.lxc.common.utils.R;
import com.lxc.frankmall.order.constant.OrderConstant;
import com.lxc.frankmall.order.entity.OrderItemEntity;
import com.lxc.frankmall.order.enume.OrderStatusEnum;
import com.lxc.frankmall.order.feign.CartFeignService;
import com.lxc.frankmall.order.feign.MemberFeignService;
import com.lxc.frankmall.order.feign.ProductFeignService;
import com.lxc.frankmall.order.feign.WmsFeignService;
import com.lxc.frankmall.order.interceptor.LoginUserInterceptor;
import com.lxc.frankmall.order.service.OrderItemService;
import com.lxc.frankmall.order.to.OrderCreateTo;
import com.lxc.frankmall.order.to.SpuInfoVo;
import com.lxc.frankmall.order.vo.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxc.common.utils.PageUtils;
import com.lxc.common.utils.Query;

import com.lxc.frankmall.order.dao.OrderDao;
import com.lxc.frankmall.order.entity.OrderEntity;
import com.lxc.frankmall.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    private final String CART_PREFIX = "gulimall:cart:";

    private ThreadLocal<OrderSubmitVo> threadLocal = new ThreadLocal<>();
    @Autowired
    MemberFeignService memberFeignService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    OrderItemService orderItemService;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    CartFeignService cartFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    WmsFeignService wmsFeignService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        MemberEntity memberEntity = LoginUserInterceptor.loginUser.get();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ObjectMapper objectMapper = new ObjectMapper();
        CompletableFuture<Void> getAddressFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            // ???????????????????????????????????????
            List<MemberAddressVo> address = memberFeignService.getAddress(memberEntity.getId());
            orderConfirmVo.setMemberAddressVos(address);
        }, executor);


        CompletableFuture<Void> cartFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            // ???????????????????????????????????????
            List<OrderItemVo> currentUserCartItems = cartFeignService.getCurrentUserCartItems();
            orderConfirmVo.setItems(currentUserCartItems);
        }, executor).thenRunAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<OrderItemVo> items = orderConfirmVo.getItems();
            List<Long> collect = items.stream().map(item -> item.getSkuId()).collect(Collectors.toList());
            R r = wmsFeignService.getSkuHasStock(collect);
            Object data = r.get("data");
            try {
                List<SkuStockVo> skuStockVos = objectMapper.convertValue(data, new TypeReference<List<SkuStockVo>>() {
                });
                if (skuStockVos != null) {
                    Map<Long, Boolean> collect1 = skuStockVos.stream().collect(Collectors.toMap((item) -> item.getSkuId(), item -> item.getHasStock()));
                    orderConfirmVo.setStocks(collect1);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }

        }, executor);


        // ??????????????????
        Integer integration = memberEntity.getIntegration();
        orderConfirmVo.setIntegration(integration);

        //TODO ????????????
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberEntity.getId(), token, 30, TimeUnit.MINUTES);

        orderConfirmVo.setOrderToken(token);
        CompletableFuture.allOf(getAddressFuture, cartFuture).join();


        return orderConfirmVo;
    }

    // @GlobalTransactional
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo vo) {
        threadLocal.set(vo);
        try {

            SubmitOrderResponseVo submitOrderResponseVo = new SubmitOrderResponseVo();
            submitOrderResponseVo.setCode(0);
            MemberEntity memberEntity = LoginUserInterceptor.loginUser.get();
            // ????????????
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

            String orderToken = vo.getOrderToken();
            // if(orderToken!=null&& orderToken.equals(s)){
            //     redisTemplate.delete(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberEntity.getId())
            // }else {
            //
            // }
            Long result = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberEntity.getId()),
                    orderToken);
            if (result.equals(0L)) {
                submitOrderResponseVo.setCode(1);
                return submitOrderResponseVo;
            } else {
                // ??????????????????
                OrderCreateTo order = createOrder(vo);
                BigDecimal payAmount = order.getOrder().getPayAmount();
                if (Math.abs(payAmount.subtract(vo.getPayPrice()).doubleValue()) < 0.01) {
                    // ??????????????????
                    saveOrder(order);
                    // ?????????
                    WareSkuLockVo lockVo = new WareSkuLockVo();
                    lockVo.setOrderSn(order.getOrder().getOrderSn());

                    //???????????????????????????????????????
                    List<OrderItemVo> orderItemVos = order.getOrderItems().stream().map((item) -> {
                        OrderItemVo orderItemVo = new OrderItemVo();
                        orderItemVo.setSkuId(item.getSkuId());
                        orderItemVo.setCount(item.getSkuQuantity());
                        orderItemVo.setTitle(item.getSkuName());
                        return orderItemVo;
                    }).collect(Collectors.toList());
                    lockVo.setLocks(orderItemVos);
                    //TODO ?????????????????????????????????
                    //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????(???????????????seata)
                    //???????????????????????????????????????seata???????????????????????????????????????????????????,??????????????????????????????
                    R r = wmsFeignService.orderLockStock(lockVo);
                    if (r.getCode() == 0) {
                        //????????????
                        submitOrderResponseVo.setOrder(order.getOrder());
                        // int i = 10 / 0;

                        //TODO ????????????????????????????????????MQ
                        rabbitTemplate.convertAndSend("order.event.exchange","order.create.order",order.getOrder());
                        //???????????????????????????
                        redisTemplate.delete(CART_PREFIX + memberEntity.getId());
                        return submitOrderResponseVo;
                    } else {
                        //????????????
                        String msg = (String) r.get("msg");
                        throw new NoStockException(msg);
                        // responseVo.setCode(3);
                        // return responseVo;
                    }

                } else {
                    submitOrderResponseVo.setCode(2);
                    return submitOrderResponseVo;
                }
            }
        } finally {
            threadLocal.remove();
        }


    }

    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        OrderEntity one = this.getOne(new LambdaQueryWrapper<OrderEntity>()
                .eq(OrderEntity::getOrderSn, orderSn));
        return one;
    }

    @Override
    public void closeOrder(OrderEntity orderEntity) {
        //?????????????????????????????????????????????????????????????????????????????????
        OrderEntity orderInfo = this.getOne(new QueryWrapper<OrderEntity>().
                eq("order_sn",orderEntity.getOrderSn()));

        if (orderInfo.getStatus().equals(OrderStatusEnum.CREATE_NEW.getCode())) {
            //???????????????????????????
            OrderEntity orderUpdate = new OrderEntity();
            orderUpdate.setId(orderInfo.getId());
            orderUpdate.setStatus(OrderStatusEnum.CANCLED.getCode());
            this.updateById(orderUpdate);

            // ???????????????MQ
            OrderTo orderTo = new OrderTo();
            BeanUtils.copyProperties(orderInfo, orderTo);

            try {
                //TODO ?????????????????????????????????????????????????????????????????????(???????????????????????????????????????)?????????????????????????????????
                rabbitTemplate.convertAndSend("order.event.exchange", "order.release.other", orderTo);
            } catch (Exception e) {
                //TODO ???????????????????????????????????????????????????
            }
        }
    }

    @Override
    public void createSeckillOrder(SeckillOrderTo orderTo) {
        // todo??????????????????
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(orderTo.getOrderSn());
        orderEntity.setMemberId(orderTo.getMemberId());
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        BigDecimal multiply = orderTo.getSeckillPrice().multiply(new BigDecimal(orderTo.getNum()));
        orderEntity.setPayAmount(multiply);
        this.save(orderEntity);
        // TODO ?????????????????????
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.setOrderSn(orderTo.getOrderSn());
        orderItemEntity.setRealAmount(multiply);
        orderItemEntity.setSkuQuantity(orderTo.getNum());
        orderItemService.save(orderItemEntity);



    }

    private void saveOrder(OrderCreateTo order2) {
        //??????????????????
        OrderEntity order = order2.getOrder();
        order.setModifyTime(new Date());
        order.setCreateTime(new Date());
        //????????????
        this.baseMapper.insert(order);

        //?????????????????????
        List<OrderItemEntity> orderItems = order2.getOrderItems();
        //???????????????????????????
        orderItemService.saveBatch(orderItems);

    }

    private OrderCreateTo createOrder(OrderSubmitVo vo) {
        OrderCreateTo orderCreateTo = new OrderCreateTo();
        String timeId = IdWorker.getTimeId();
        // ?????????????????????
        OrderEntity orderEntity = buildOrder(timeId);
        // ???????????????????????????
        List<OrderItemEntity> itemEntities = buildOrderItems(timeId);
        // ?????????????????????
        computePrice(orderEntity, itemEntities);
        orderCreateTo.setOrder(orderEntity);
        orderCreateTo.setOrderItems(itemEntities);


        return orderCreateTo;
    }

    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> itemEntities) {
        // ??????????????????
        BigDecimal total = BigDecimal.ZERO;
        //?????????
        BigDecimal coupon = new BigDecimal("0.0");
        BigDecimal intergration = new BigDecimal("0.0");
        BigDecimal promotion = new BigDecimal("0.0");
        //??????????????????
        Integer integrationTotal = 0;
        Integer growthTotal = 0;
        for (OrderItemEntity itemEntity : itemEntities) {
            coupon = coupon.add(itemEntity.getCouponAmount());
            promotion = promotion.add(itemEntity.getPromotionAmount());
            intergration = intergration.add(itemEntity.getIntegrationAmount());

            //??????
            total = total.add(itemEntity.getRealAmount());

            //??????????????????????????????
            integrationTotal += itemEntity.getGiftIntegration();
            growthTotal += itemEntity.getGiftGrowth();

        }
        orderEntity.setTotalAmount(total);
        // ??????????????????
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));
        orderEntity.setCouponAmount(coupon);
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setIntegrationAmount(intergration);

        //???????????????????????????
        orderEntity.setIntegration(integrationTotal);
        orderEntity.setGrowth(growthTotal);
        //??????????????????(0-????????????1-?????????)
        orderEntity.setDeleteStatus(0);

    }

    private OrderEntity buildOrder(String timeId) {
        MemberEntity memberEntity = LoginUserInterceptor.loginUser.get();
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(timeId);
        orderEntity.setMemberId(memberEntity.getId());
        orderEntity.setMemberUsername(memberEntity.getUsername());
        // ??????????????????
        OrderSubmitVo orderSubmitVo = threadLocal.get();
        R r = wmsFeignService.getFare(orderSubmitVo.getAddrId());
        Object data = r.get("data");
        FareVo fareVo = new ObjectMapper().convertValue(data, FareVo.class);
        // ????????????
        orderEntity.setFreightAmount(fareVo.getFare());
        //???????????????
        orderEntity.setReceiverCity(fareVo.getAddress().getCity());
        orderEntity.setReceiverDetailAddress(fareVo.getAddress().getDetailAddress());
        orderEntity.setReceiverName(fareVo.getAddress().getName());
        orderEntity.setReceiverPhone(fareVo.getAddress().getPhone());
        orderEntity.setReceiverPostCode(fareVo.getAddress().getPostCode());
        orderEntity.setReceiverProvince(fareVo.getAddress().getProvince());
        orderEntity.setReceiverRegion(fareVo.getAddress().getRegion());
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setAutoConfirmDay(7);
        return orderEntity;
    }

    /**
     * ???????????????
     *
     * @param timeId
     * @return
     */
    private List<OrderItemEntity> buildOrderItems(String timeId) {
        // ????????????????????????????????????
        List<OrderItemVo> currentUserCartItems = cartFeignService.getCurrentUserCartItems();
        if (currentUserCartItems != null && currentUserCartItems.size() > 0) {
            List<OrderItemEntity> itemEntities = currentUserCartItems.stream().map(currentUserCartItem -> {
                OrderItemEntity itemEntity = buildOrderItem(currentUserCartItem);
                itemEntity.setOrderSn(timeId);
                return itemEntity;
            }).collect(Collectors.toList());
            return itemEntities;
        }
        return null;
    }

    private OrderItemEntity buildOrderItem(OrderItemVo currentUserCartItem) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        // ???????????????????????????OK

        // spu??????
        Long skuId = currentUserCartItem.getSkuId();
        R r = productFeignService.getSpuInfoBySkuId(skuId);
        Object data = r.get("data");
        SpuInfoVo spuInfoVo = new ObjectMapper().convertValue(data, SpuInfoVo.class);
        orderItemEntity.setSpuId(spuInfoVo.getId());
        orderItemEntity.setSpuBrand(spuInfoVo.getBrandId().toString());
        orderItemEntity.setSpuName(spuInfoVo.getSpuName());
        orderItemEntity.setCategoryId(spuInfoVo.getCatalogId());

        // sku??????
        orderItemEntity.setSkuId(currentUserCartItem.getSkuId());
        orderItemEntity.setSkuName(currentUserCartItem.getTitle());
        orderItemEntity.setSkuPic(currentUserCartItem.getImage());
        orderItemEntity.setSkuPrice(currentUserCartItem.getPrice());
        orderItemEntity.setSkuAttrsVals(StringUtils.collectionToDelimitedString(currentUserCartItem.getSkuAttrValues(), ";"));
        orderItemEntity.setSkuQuantity(currentUserCartItem.getCount());


        // ????????????????????????

        // ????????????
        orderItemEntity.setGiftGrowth(currentUserCartItem.getPrice().intValue());
        orderItemEntity.setGiftIntegration(currentUserCartItem.getPrice().intValue());
        // ????????????????????????
        orderItemEntity.setPromotionAmount(BigDecimal.ZERO);
        orderItemEntity.setCouponAmount(BigDecimal.ZERO);
        orderItemEntity.setIntegrationAmount(BigDecimal.ZERO);
        // ??????????????????????????????
        BigDecimal multiply = orderItemEntity.getSkuPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity()));
        orderItemEntity.setRealAmount(multiply
                .subtract(orderItemEntity.getPromotionAmount())
                .subtract(orderItemEntity.getCouponAmount())
                .subtract(orderItemEntity.getIntegrationAmount()));


        return orderItemEntity;
    }

}