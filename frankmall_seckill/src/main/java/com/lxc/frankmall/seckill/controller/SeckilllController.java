package com.lxc.frankmall.seckill.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lxc.common.utils.R;
import com.lxc.frankmall.seckill.service.SecKillService;
import com.lxc.frankmall.seckill.to.SeckillSkuRedisTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Frank_lin
 * @date 2022/7/6
 */
@Controller
public class SeckilllController {


    @Autowired
    SecKillService secKillService;

    /**
     * 当前时间可以参与秒杀的商品信息
     *
     * @return
     */
    @GetMapping("/getCurrentSeckillSkus")
    @ResponseBody
    public R getCurrentSecKillSkus() {
        List<SeckillSkuRedisTo> vos = secKillService.getCurrentSecKillSkus();
        return R.ok().put("data", vos);
    }


    @GetMapping("/sku/seckill/{skuId}")
    @ResponseBody
    public R getSkuSeckillInfo(@PathVariable("skuId") Long skuId) throws JsonProcessingException {
        SeckillSkuRedisTo to = secKillService.getSkuSeckillInfo(skuId);


        return R.ok().put("data", to);
    }


    @GetMapping("/kill")
    public String secKill(@RequestParam("killId") String killId,
                          @RequestParam("key") String key,
                          @RequestParam("num") Integer num,
                          Model model) throws JsonProcessingException {

        String orderSn = secKillService.kill(killId, key, num);
        model.addAttribute("orderSn",orderSn);
        return "success";
    }
}
