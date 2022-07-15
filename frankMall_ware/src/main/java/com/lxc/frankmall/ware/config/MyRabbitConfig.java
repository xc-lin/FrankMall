package com.lxc.frankmall.ware.config;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Frank_lin
 * @date 2022/7/3
 */
@Configuration
public class MyRabbitConfig {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();

    }


    @Bean
    public Queue stockDelayQueue() {
        return QueueBuilder.durable("stock.delay.queue")
                .deadLetterExchange("stock.event.exchange")
                .deadLetterRoutingKey("stock.release")
                .ttl(30000)
                .build();
    }

    @Bean
    public Queue stockReleaseStockQueue() {
        return QueueBuilder.durable("stock.release.stock.queue")
                .build();
    }

    @Bean
    public Exchange stockEventExchange() {
        return ExchangeBuilder.topicExchange("stock.event.exchange")
                .durable(true)
                .build();
    }

    @Bean
    public Binding stockLockedBinding(@Qualifier("stockDelayQueue") Queue queue,
                                           @Qualifier("stockEventExchange") Exchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("stock.locked")
                .noargs();
    }

    @Bean
    public Binding stockReleaseBinding(@Qualifier("stockReleaseStockQueue") Queue queue,
                                            @Qualifier("stockEventExchange") Exchange exchange) {

        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("stock.release.#")
                .noargs();
    }

}
