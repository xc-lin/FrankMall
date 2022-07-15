package com.lxc.frankmall.product;

import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

/**
 * @author Frank_lin
 * @date 2022/6/28
 */
public class ThreadTest {
    public static ExecutorService executorService = Executors.newScheduledThreadPool(10);

    @Test
    public void testCallable() throws ExecutionException, InterruptedException {
        FutureTask<Integer> futureTask = new FutureTask<>(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                try {
                    TimeUnit.MILLISECONDS.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return 10;

            }
        });
        new Thread(futureTask).start();
        System.out.println(futureTask.get());
    }


    @Test
    public void testCallable2() throws ExecutionException, InterruptedException {


        FutureTask<Integer> futureTask = new FutureTask<>(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 10;

        });
        executorService.submit(futureTask);
        System.out.println(futureTask.get());
    }


    @Test
    public void testC2() throws ExecutionException, InterruptedException {
        // CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
        //     System.out.println(11111);
        //     int a= 10/0;
        //     return 111;
        // }, executorService).whenComplete((result,exception)->{
        //     // 虽然能得到异常消息，但是没法修改返回结果
        //     System.out.println(result);
        //     System.out.println(exception);
        // }).exceptionally((e)->{
        //     // 可以获得异常消息，并返回默认值
        //     System.out.println(e);
        //     return 10;
        // });

        // System.out.println(integerCompletableFuture.get());


        // CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
        //     System.out.println(11111);
        //     int a= 10/2;
        //     return 111;
        // }, executorService).handle((result,exception)->{
        //     // 完成后的处理，可以修改结果
        //     System.out.println(result);
        //     if (result!=null){
        //         return result;
        //     }
        //     if (exception!=null){
        //         return 999;
        //     }
        //     return -1;
        // });
        // System.out.println(integerCompletableFuture.get());


        // CompletableFuture<String> integerCompletableFuture =  CompletableFuture.supplyAsync(() -> {
        //     System.out.println(11111);
        //     try {
        //         TimeUnit.MILLISECONDS.sleep(1000);
        //     } catch (InterruptedException e) {
        //         e.printStackTrace();
        //     }
        //
        //     int a = 10 / 2;
        //     System.out.println("任务一执行了");
        //     return 111;
        // }, executorService).thenApplyAsync((result) -> {
        //     System.out.println(result);
        //     System.out.println("任务二执行了");
        //     return "linxc";
        // });
        //
        // System.out.println(integerCompletableFuture.get());
        // try {
        //     TimeUnit.MILLISECONDS.sleep(10000);
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }


        // CompletableFuture<String> integerCompletableFuture =  CompletableFuture.supplyAsync(() -> {
        //     System.out.println(11111);
        //     try {
        //         TimeUnit.MILLISECONDS.sleep(1000);
        //     } catch (InterruptedException e) {
        //         e.printStackTrace();
        //     }
        //
        //     int a = 10 / 2;
        //     System.out.println("任务一执行了");
        //     return 111;
        // }, executorService).thenCombine(CompletableFuture.supplyAsync(()->{
        //     return "helllo";
        // }),(result1,result2)->{
        //     System.out.println("result1: "+result1);
        //     System.out.println("result2: "+result2);
        //     return "result3";
        // });
        // System.out.println(integerCompletableFuture.get());


        // CompletableFuture<String> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
        //     System.out.println(11111);
        //     try {
        //         TimeUnit.MILLISECONDS.sleep(1000);
        //     } catch (InterruptedException e) {
        //         e.printStackTrace();
        //     }
        //
        //     int a = 10 / 2;
        //     System.out.println("任务一执行了");
        //     return 111;
        // }, executorService).applyToEitherAsync(CompletableFuture.supplyAsync(() -> {
        //     return 2222;
        // }), (result2) -> {
        //     System.out.println("result: " + result2);
        //     return "result3";
        // });
        // System.out.println(integerCompletableFuture.get());


        CompletableFuture<String> futureImg = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品图片信息 ");
            return "hello.jpg";
        },executorService);
        CompletableFuture<String> futureAttr = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品属性信息 ");
            return "黑色+256Gb";
        },executorService);
        CompletableFuture<String> futureDesc = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(3000);
                System.out.println("查询商品介绍信息 ");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            return "华为";
        });
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(futureImg, futureAttr, futureDesc);

        voidCompletableFuture.get();
        // System.out.println(futureImg.get()+futureAttr.get()+futureDesc.get());
    }
}
