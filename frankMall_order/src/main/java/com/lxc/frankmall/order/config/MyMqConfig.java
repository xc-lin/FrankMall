package com.lxc.frankmall.order.config;

import com.lxc.frankmall.order.entity.OrderEntity;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Frank_lin
 * @date 2022/7/6
 */
@Configuration
public class MyMqConfig {

    @Bean
    public Queue orderDelayQueue() {
        return QueueBuilder.durable("order.delay.queue")
                .deadLetterExchange("order.event.exchange")
                .deadLetterRoutingKey("order.release.order")
                .ttl(60000)
                .build();
    }

    @Bean
    public Queue orderReleaseQueue() {
        return QueueBuilder.durable("order.release.order.queue")
                .build();
    }


    @Bean
    public Exchange orderEventExchange() {
        return ExchangeBuilder.topicExchange("order.event.exchange")
                .durable(true)
                .build();
    }

    @Bean
    public Binding orderCreateOrderBinding(@Qualifier("orderDelayQueue") Queue queue,
                                           @Qualifier("orderEventExchange") Exchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("order.create.order")
                .noargs();
    }

    @Bean
    public Binding orderReleaseOrderBinding(@Qualifier("orderReleaseQueue") Queue queue,
                                            @Qualifier("orderEventExchange") Exchange exchange) {

        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("order.release.order")
                .noargs();
    }

    @Bean
    public Binding orderReleaseOtherBinding() {

        return new Binding("stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                "order.event.exchange",
                "order.release.other.#",
                null);
    }

    @Bean
    public Queue orderSeckillOrderQueue() {
        return QueueBuilder.durable("order.seckill.order.queue")
                .build();
    }

    @Bean
    public Binding orderSeckillOrderQueueBinding(@Qualifier("orderSeckillOrderQueue")Queue queue,
                                                 @Qualifier("orderEventExchange") Exchange exchange) {

        return BindingBuilder.bind(queue).to(exchange).with("order.seckill.order").noargs();
    }



}
