package com.lxc.frankmall.product.web;

import com.lxc.frankmall.product.service.SkuInfoService;
import com.lxc.frankmall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Frank_lin
 * @date 2022/6/28
 */
@Controller
public class ItemController {
    @Autowired
    SkuInfoService skuInfoService;

    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model){
        System.out.println("准备查询"+skuId+"详情");

        SkuItemVo skuItemVo=  skuInfoService.item(skuId);
        model.addAttribute("item",skuItemVo);
        System.out.println(skuItemVo);
        return "item";
    }
}
