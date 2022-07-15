package com.lxc.frankmall.seckill.scheduled;

import com.lxc.frankmall.seckill.service.SecKillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author Frank_lin
 * @date 2022/7/6
 */

/**
 * 秒杀商品定时上架
 * 每天晚上3点，上架最近三天需要三天秒杀的商品
 * 当天00:00:00 - 23:59:59
 * 明天00:00:00 - 23:59:59
 * 后天00:00:00 - 23:59:59
 */

@Slf4j
@Service
public class SeckillSkuScheduled {

    @Autowired
    SecKillService secKillService;

    @Autowired
    RedissonClient redissonClient;

    private final String UPLOAD_LOCK = "seckill:upload:lock";

    @Scheduled(cron = "* */20 * * * ?")
    public void uploadSeckillSkuLatest3Days() {
        RLock lock = redissonClient.getLock(UPLOAD_LOCK);
        lock.lock(10, TimeUnit.SECONDS);
        try {
            // 重复上架无需处理
            log.info("上架秒杀的商品");
            secKillService.uploadSeckillSkuLatest3Days();
        }finally {
            lock.unlock();
        }


    }
}
