package com.lxc.cart.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lxc.cart.vo.Cart;
import com.lxc.cart.vo.CartItem;

import java.util.List;

/**
 * @author Frank_lin
 * @date 2022/7/1
 */
public interface CartService {
    CartItem addToCart(Long skuId, int num) throws JsonProcessingException;

    CartItem getCartItem(Long skuId) throws JsonProcessingException;

    Cart getCart() throws JsonProcessingException;

    void checkItem(Long skuId, Integer check);

    void changeItemCount(Long skuId, Integer num);

    void deleteItem(Long skuId);

    List<CartItem> getUserCartItems();
}
