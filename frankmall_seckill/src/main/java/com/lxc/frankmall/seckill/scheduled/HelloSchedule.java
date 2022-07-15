package com.lxc.frankmall.seckill.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Frank_lin
 * @date 2022/7/6
 */

/**
 * 定时任务
 *      1、@EnableScheduling 开启定时任务
 *      2、@Scheduled开启一个定时任务
 *
 * 异步任务
 *      1、@EnableAsync:开启异步任务
 *      2、@Async：给希望异步执行的方法标注
 */

@Slf4j
@Component
public class HelloSchedule {
    /**
     * 1、在Spring中表达式是6位组成，不允许第七位的年份
     * 2、在周几的的位置,1-7代表周一到周日
     * 3、定时任务不该阻塞。默认是阻塞的
     *      1）、可以让业务以异步的方式，自己提交到线程池
     *              CompletableFuture.runAsync(() -> {
     *         },execute);
     *
     *      2）、支持定时任务线程池；设置 TaskSchedulingProperties
     *        spring.task.scheduling.pool.size: 5
     *
     *      3）、让定时任务异步执行
     *          异步任务
     *
     *      解决：使用异步任务 + 定时任务来完成定时任务不阻塞的功能
     *
     */
    // @Async
    // @Scheduled(cron = "* * * * * ?")
    public  void hello(){
        // CompletableFuture.runAsync(()->{
        //     log.info("1111");
        // })
        log.info("1111");
        try {
            TimeUnit.MILLISECONDS.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }



}
