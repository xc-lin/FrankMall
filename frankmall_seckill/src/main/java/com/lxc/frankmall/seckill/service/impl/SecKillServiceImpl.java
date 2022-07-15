package com.lxc.frankmall.seckill.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxc.common.entity.MemberEntity;
import com.lxc.common.to.mq.SeckillOrderTo;
import com.lxc.common.utils.R;
import com.lxc.frankmall.seckill.feign.CouponFeignService;
import com.lxc.frankmall.seckill.feign.ProductFeignService;
import com.lxc.frankmall.seckill.interceptor.LoginUserInterceptor;
import com.lxc.frankmall.seckill.service.SecKillService;
import com.lxc.frankmall.seckill.to.SeckillSkuRedisTo;
import com.lxc.frankmall.seckill.vo.SeckillSessionWithSkusVo;
import com.lxc.frankmall.seckill.vo.SeckillSkuVo;
import com.lxc.frankmall.seckill.vo.SkuInfoVo;
import org.apache.commons.lang.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Frank_lin
 * @date 2022/7/6
 */
@Service
public class SecKillServiceImpl implements SecKillService {


    private final String SESSIONS_CACHE_PREFIX = "seckill:sessions:";
    private final String SKUKILL_CACHE_PREFIX = "seckill:skus:";
    // + 商品随机码
    private final String SKU_STOCK_SEMAPHORE = "seckill:stock:";

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Override
    public void uploadSeckillSkuLatest3Days() {
        // 扫描最近三天需要参与的活动
        R r = couponFeignService.getLatest3DaySession();
        if (r.getCode() == 0) {
            Object data = r.get("data");
            List<SeckillSessionWithSkusVo> seckillSessionWithSkusVos = new ObjectMapper().convertValue(data, new TypeReference<List<SeckillSessionWithSkusVo>>() {
            });
            // 缓存到redis中
            // 缓存活动信息
            saveSessionInfo(seckillSessionWithSkusVos);
            // 保存活动商品信息
            saveSessionSkuInfo(seckillSessionWithSkusVos);


        }


    }

    @Override
    public List<SeckillSkuRedisTo> getCurrentSecKillSkus() {
        ObjectMapper objectMapper = new ObjectMapper();
        //1、确定当前属于哪个秒杀场次
        long time = new Date().getTime();
        Set<String> keys = redisTemplate.keys(SESSIONS_CACHE_PREFIX + "*");
        for (String key : keys) {
            String replace = key.replace(SESSIONS_CACHE_PREFIX, "");
            String[] s = replace.split("_");
            Long start = Long.parseLong(s[0]);
            Long end = Long.parseLong(s[1]);
            if (time >= start && time <= end) {
                // 获取这个秒杀场次需要的所有商品信息
                List<String> range = redisTemplate.opsForList().range(key, 0, -1);
                BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
                List<String> objects = hashOps.multiGet(range);
                if (objects != null) {
                    List<SeckillSkuRedisTo> collect = objects.stream().map(item -> {

                        SeckillSkuRedisTo seckillSkuRedisTo = null;
                        try {
                            seckillSkuRedisTo = objectMapper.readValue(item, SeckillSkuRedisTo.class);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                        // redisTo.setRandomCode(null);当前秒杀开始需要随机码
                        return seckillSkuRedisTo;
                    }).collect(Collectors.toList());
                    return collect;
                }
                break;
            }
        }
        return null;
    }

    @Override
    public SeckillSkuRedisTo getSkuSeckillInfo(Long skuId) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        // 找到所有需要参与秒杀的商品的key
        BoundHashOperations<String, String, String> operations = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        Set<String> keys = operations.keys();
        if (keys != null && keys.size() > 0) {
            String reg = "\\d_" + skuId;
            for (String key : keys) {
                if (Pattern.matches(reg, key)) {
                    String json = operations.get(key);
                    SeckillSkuRedisTo seckillSkuRedisTo = objectMapper.readValue(json, SeckillSkuRedisTo.class);
                    Long startTime = seckillSkuRedisTo.getStartTime();
                    Long endTime = seckillSkuRedisTo.getEndTime();
                    long l = System.currentTimeMillis();
                    if (l >= startTime && l <= endTime) {

                    } else {
                        seckillSkuRedisTo.setRandomCode(null);
                    }
                    return seckillSkuRedisTo;
                }
            }
        }
        return null;
    }

    @Override
    public String kill(String killId, String key, Integer num) throws JsonProcessingException {
        MemberEntity memberEntity = LoginUserInterceptor.loginUser.get();
        ObjectMapper objectMapper = new ObjectMapper();
        // 获取当前秒杀商品的详细信息
        BoundHashOperations<String, String, String> operations = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        String s = operations.get(killId);
        if (StringUtils.isNotEmpty(s)) {
            SeckillSkuRedisTo seckillSkuRedisTo = objectMapper.readValue(s, SeckillSkuRedisTo.class);
            // 合法性
            Long startTime = seckillSkuRedisTo.getStartTime();
            Long endTime = seckillSkuRedisTo.getEndTime();
            long l = System.currentTimeMillis();
            // 时间的合法性
            if (l >= startTime && l <= endTime) {
                String randomCode = seckillSkuRedisTo.getRandomCode();
                String skuId = seckillSkuRedisTo.getPromotionSessionId() + "_" + seckillSkuRedisTo.getSkuId();
                if (randomCode.equals(key) && skuId.equals(killId)) {
                    // 验证购物数量是否合理
                    Integer seckillLimit = seckillSkuRedisTo.getSeckillLimit();
                    if (num <= seckillLimit) {
                        // 验证这个人是否已经购买过 如果秒杀成功，就去redis中占位
                        String redisKey = memberEntity.getId() + "_" + skuId;
                        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(redisKey, num.toString(), endTime - l, TimeUnit.MILLISECONDS);
                        if (aBoolean) {
                            RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + randomCode);
                            try {
                                boolean b = semaphore.tryAcquire(num);
                                // 快速下单

                                if (b) {
                                    String timeId = UUID.randomUUID().toString();
                                    SeckillOrderTo seckillOrderTo = new SeckillOrderTo();
                                    seckillOrderTo.setOrderSn(timeId);
                                    seckillOrderTo.setPromotionSessionId(seckillSkuRedisTo.getPromotionSessionId());
                                    seckillOrderTo.setNum(num);
                                    seckillOrderTo.setMemberId(memberEntity.getId());
                                    seckillOrderTo.setSkuId(seckillSkuRedisTo.getSkuId());
                                    seckillOrderTo.setSeckillPrice(seckillSkuRedisTo.getSeckillPrice());

                                    rabbitTemplate.convertAndSend("order.event.exchange","order.seckill.order",seckillOrderTo);

                                    return timeId;
                                }
                            } catch (Exception e) {
                                return null;
                            } finally {

                            }
                        }
                    }

                }
            } else {
                return null;
            }
        }

        return null;
    }

    private void saveSessionInfo(List<SeckillSessionWithSkusVo> vos) {
        vos.stream().forEach(session -> {
            long start = session.getStartTime().getTime();
            long end = session.getEndTime().getTime();
            String key = SESSIONS_CACHE_PREFIX + start + "_" + end;
            Boolean aBoolean = redisTemplate.hasKey(key);
            if (!aBoolean) {
                List<String> collect = session.getRelationSkus().stream().map(item -> item.getId() + "_" + item.getSkuId().toString()).collect(Collectors.toList());
                // 缓存活动信息
                redisTemplate.opsForList().leftPushAll(key, collect);
            }
        });
    }


    private void saveSessionSkuInfo(List<SeckillSessionWithSkusVo> vos) {
        ObjectMapper objectMapper = new ObjectMapper();
        vos.stream().forEach(session -> {

                    // 准备hash操作
                    BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
                    session.getRelationSkus().stream().forEach(seckillSkuVo -> {
                        SeckillSkuRedisTo seckillSkuRedisTo = new SeckillSkuRedisTo();
                        // 随机码
                        String uuid = UUID.randomUUID().toString().replace("_", "");
                        if (!operations.hasKey(seckillSkuVo.getPromotionSessionId().toString() + "_" + seckillSkuVo.getSkuId().toString())) {

                            // sku的基本数据
                            R r = productFeignService.getSkuInfo(seckillSkuVo.getSkuId());
                            if (r.getCode() == 0) {
                                Object obj = r.get("skuInfo");
                                SkuInfoVo skuInfoVo = objectMapper.convertValue(obj, SkuInfoVo.class);
                                seckillSkuRedisTo.setSkuInfo(skuInfoVo);
                            }

                            // sku的秒杀信息
                            BeanUtils.copyProperties(seckillSkuVo, seckillSkuRedisTo);
                            // 设置上商品的秒杀时间信息
                            seckillSkuRedisTo.setStartTime(session.getStartTime().getTime());
                            seckillSkuRedisTo.setEndTime(session.getEndTime().getTime());
                            seckillSkuRedisTo.setRandomCode(uuid);


                            String s = null;
                            try {
                                s = objectMapper.writeValueAsString(seckillSkuRedisTo);
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                            operations.put(seckillSkuVo.getPromotionSessionId().toString() + "_" + seckillSkuVo.getSkuId().toString(), s);

                            // 如果当前这个场次的库存信息已经上架，就不需要上架
                            RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + uuid);
                            // 商品可以秒杀的数量作为信号量 -> 限流
                            semaphore.trySetPermits(seckillSkuRedisTo.getSeckillCount());
                        }


                    });
                }
        );

    }

}
