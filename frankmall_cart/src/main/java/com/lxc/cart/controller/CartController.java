package com.lxc.cart.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lxc.cart.interceptor.CartInterceptor;
import com.lxc.cart.service.CartService;
import com.lxc.cart.vo.Cart;
import com.lxc.cart.vo.CartItem;
import com.lxc.cart.vo.UserInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author Frank_lin
 * @date 2022/7/1
 */
@Controller
public class CartController {

    @Autowired
    CartService cartService;


    @GetMapping("/currentUserCartItems")
    @ResponseBody
    public List<CartItem> getCurrentUserCartItems() {

        return cartService.getUserCartItems();

    }


    @GetMapping("/cart.html")
    public String cartListPage(Model model) throws JsonProcessingException {
        Cart cart = cartService.getCart();
        model.addAttribute("cart", cart);

        return "cartList";
    }

    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId,
                            @RequestParam("num") int num) throws JsonProcessingException {
        CartItem cartItem = cartService.addToCart(skuId, num);
        return "redirect:http://cart.gulimall.com/addToCartSuccess.html?skuId=" + skuId;
    }

    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId,
                                       Model model) throws JsonProcessingException {
        // 再次查询购物车数据
        CartItem cartItem = cartService.getCartItem(skuId);
        model.addAttribute("cartItem", cartItem);
        return "success";
    }

    @GetMapping("/checkItem")
    public String checkItem(@RequestParam("skuId") Long skuId, @RequestParam("checked") Integer check) {
        cartService.checkItem(skuId, check);

        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId") Long skuId,
                            @RequestParam("num") Integer num) {
        cartService.changeItemCount(skuId, num);

        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId) {
        cartService.deleteItem(skuId);

        return "redirect:http://cart.gulimall.com/cart.html";
    }

}
