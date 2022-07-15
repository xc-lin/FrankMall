package com.lxc.cart.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxc.cart.feign.ProductFeignService;
import com.lxc.cart.interceptor.CartInterceptor;
import com.lxc.cart.service.CartService;
import com.lxc.cart.to.SkuInfoEntity;
import com.lxc.cart.vo.Cart;
import com.lxc.cart.vo.CartItem;
import com.lxc.cart.vo.UserInfoTo;
import com.lxc.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author Frank_lin
 * @date 2022/7/1
 */
@Slf4j
@Service
public class CartServiceImpl implements CartService {


    @Autowired
    ProductFeignService productFeignService;



    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ThreadPoolExecutor executor;


    private final String CART_PREFIX = "gulimall:cart:";

    @Override
    public CartItem addToCart(Long skuId, int num) throws JsonProcessingException {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String result = (String) cartOps.get(skuId.toString());
        CartItem cartItem = new CartItem();
        ObjectMapper objectMapper = new ObjectMapper();
        if (result == null) {
            // 购物车有这个商品


            // 商品添加到购物车


            CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(() -> {
                // 远程查询当前要查询的商品的信息
                R r = productFeignService.getSkuInfo(skuId);
                Object skuInfo = r.get("skuInfo");
                SkuInfoEntity skuInfoEntity = objectMapper.convertValue(skuInfo, new TypeReference<SkuInfoEntity>() {
                });
                cartItem.setCheck(true);
                cartItem.setCount(num);
                cartItem.setImage(skuInfoEntity.getSkuDefaultImg());
                cartItem.setTitle(skuInfoEntity.getSkuTitle());
                cartItem.setSkuId(skuId);
                cartItem.setPrice(skuInfoEntity.getPrice());
            }, executor);

            CompletableFuture<Void> getSkuSaleAttrValues = CompletableFuture.runAsync(() -> {
                List<String> skuSaleAttr = productFeignService.getSkuSaleAttr(skuId);
                cartItem.setSkuAttrValues(skuSaleAttr);
            }, executor);
            CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(getSkuInfoTask, getSkuSaleAttrValues);
            voidCompletableFuture.join();
            String s = objectMapper.writeValueAsString(cartItem);
            cartOps.put(skuId.toString(), s);

        } else {
            CartItem cartItem1 = objectMapper.readValue(result, new TypeReference<CartItem>() {
            });
            cartItem1.setCount(cartItem1.getCount() + num);
            cartOps.put(skuId.toString(), objectMapper.writeValueAsString(cartItem1));
            return cartItem1;

        }


        return cartItem;
    }

    @Override
    public CartItem getCartItem(Long skuId) throws JsonProcessingException {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String result = (String) cartOps.get(skuId.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        CartItem cartItem = objectMapper.readValue(result, new TypeReference<CartItem>() {
        });
        return cartItem;

    }

    @Override
    public Cart getCart() throws JsonProcessingException {
        Cart cart = new Cart();
        ObjectMapper objectMapper = new ObjectMapper();
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartKey = CART_PREFIX + userInfoTo.getUserKey();
        BoundHashOperations<String, Object, Object> cartOps = redisTemplate.boundHashOps(cartKey);

        List<Object> values = cartOps.values();
        List<CartItem> collect = null;
        if (values != null && values.size() > 0) {
            collect = values.stream().map(obj -> {
                String s = (String) obj;
                CartItem cartItem = null;
                try {
                    cartItem = objectMapper.readValue(s, new TypeReference<CartItem>() {
                    });
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                return cartItem;
            }).collect(Collectors.toList());
        }
        if (userInfoTo.getUserId() != null) {
            // 登录了
            if (collect != null) {
                for (CartItem cartItem : collect) {
                    addToCart(cartItem.getSkuId(), cartItem.getCount());
                }
            }
            BoundHashOperations<String, Object, Object> cartOps1 = getCartOps();
            List<Object> values1 = cartOps1.values();
            if (values1 != null && values1.size() > 0) {
                collect = values1.stream().map(obj -> {
                    String s = (String) obj;
                    CartItem cartItem = null;
                    try {
                        cartItem = objectMapper.readValue(s, new TypeReference<CartItem>() {
                        });
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return cartItem;
                }).collect(Collectors.toList());

            }
            redisTemplate.delete(CART_PREFIX + userInfoTo.getUserKey());

        }
        cart.setItems(collect);
        return cart;
    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        try {
            CartItem cartItem = getCartItem(skuId);
            cartItem.setCheck(check == 1);
            String s = new ObjectMapper().writeValueAsString(cartItem);
            cartOps.put(skuId.toString(), s);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void changeItemCount(Long skuId, Integer num) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        try {
            CartItem cartItem = getCartItem(skuId);
            cartItem.setCount(num);
            String s = new ObjectMapper().writeValueAsString(cartItem);
            cartOps.put(skuId.toString(), s);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    @Override
    public List<CartItem> getUserCartItems() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId()==null){
            return null;
        }
        List<CartItem> cartItems = getCartItems(CART_PREFIX + userInfoTo.getUserId());
        if (cartItems!=null) {
            List<CartItem> collect = cartItems.stream()
                    .filter(item -> item.getCheck())
                    .map(item -> {
                        R r = productFeignService.getPrice(item.getSkuId());
                        String data = r.get("data").toString();

                        // 更新为最新价格
                        item.setPrice(new BigDecimal(data));
                        return item;
                    })
                    .collect(Collectors.toList());
            // 获取被选中的购物项
            return collect;

        }

        return null;
    }

    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartKey = "";
        if (userInfoTo.getUserId() != null) {
            cartKey = CART_PREFIX + userInfoTo.getUserId();
        } else {
            cartKey = CART_PREFIX + userInfoTo.getUserKey();
        }
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        return operations;
    }

    private List<CartItem> getCartItems(String cartKey) {
        //获取购物车里面的所有商品
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        List<Object> values = operations.values();
        if (values != null && values.size() > 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            List<CartItem> cartItemVoStream = values.stream().map((obj) -> {
                String str = (String) obj;
                CartItem cartItem = null;
                try {
                    cartItem = objectMapper.readValue(str, CartItem.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                return cartItem;
            }).collect(Collectors.toList());
            return cartItemVoStream;
        }
        return null;

    }

}
