package com.lxc.frankmall.order.web;

import com.lxc.frankmall.order.service.OrderService;
import com.lxc.frankmall.order.vo.OrderConfirmVo;
import com.lxc.frankmall.order.vo.OrderSubmitVo;
import com.lxc.frankmall.order.vo.SubmitOrderResponseVo;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author Frank_lin
 * @date 2022/7/3
 */
@Controller
@Lazy
public class OrderWebController {

    @Autowired
    OrderWebController orderWebController;

    @Autowired
    OrderService orderService;


    @GetMapping("/toTrade")
    public String toTrade(Model model){
        // 展示订单的信息
        OrderConfirmVo orderConfirmVo = orderService.confirmOrder();
        model.addAttribute("confirmOrderData",orderConfirmVo);
        return "confirm";
    }

    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo vo, Model model, RedirectAttributes attributes){

        // 创建订单，验令牌，验价格，锁库存
        SubmitOrderResponseVo submitOrderResponseVo =  orderService.submitOrder(vo);
        // 下单成功来到支付选择页
        System.out.println(vo);
        if(submitOrderResponseVo.getCode().equals(0)){
            model.addAttribute("submitOrderResp",submitOrderResponseVo);
            return "pay";
        }else {
            String msg = "下单失败";
            switch (submitOrderResponseVo.getCode()) {
                case 1: msg += "订单信息过期，请刷新再次提交"; break;
                case 2: msg += "订单商品价格发生变化，请确认后再次提交"; break;
                case 3: msg += "库存锁定失败，商品库存不足"; break;
            }
            attributes.addFlashAttribute("msg",msg);

            // 下单失败，回到订单确认页
            return "redirect:http://order.gulimall.com/toTrade";
        }

    }
}
