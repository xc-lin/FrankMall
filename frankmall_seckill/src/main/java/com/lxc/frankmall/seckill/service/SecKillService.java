package com.lxc.frankmall.seckill.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lxc.frankmall.seckill.to.SeckillSkuRedisTo;

import java.util.List;

/**
 * @author Frank_lin
 * @date 2022/7/6
 */

public interface SecKillService {

    void uploadSeckillSkuLatest3Days();

    List<SeckillSkuRedisTo> getCurrentSecKillSkus();

    SeckillSkuRedisTo getSkuSeckillInfo(Long skuId) throws JsonProcessingException;

    String kill(String killId, String key, Integer num) throws JsonProcessingException;
}
