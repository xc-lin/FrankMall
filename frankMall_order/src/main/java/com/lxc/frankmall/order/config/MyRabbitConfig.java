package com.lxc.frankmall.order.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author Frank_lin
 * @date 2022/7/3
 */
@Configuration
public class MyRabbitConfig {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();

    }


    /**
     * 服务器收到消息就回调
     * 消息正确抵达队列，进行回调
     * 消费端确认（保证每个消息都被正确消费，broker就可以删除这个消息）
     *  1、默认是自动确认的，只要消息接收到，客户端就会自动确认，broker就会删除这个消息
     *      问题：收到很多消息，自动回复给服务器ack，只有一个消息处理成功，宕机，发生消息丢失
     *  手动确认:
     *      只要我们没有明确告诉mq ack，消息就一直是unack，即使comsumer宕机，消息不会丢失，会重新变为ready，下一次有新的consumer进来，消息就会发给他
     */
    @PostConstruct
    public void initRabbitTemplate(){
        // 设置确认回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             * 只要消息抵达broker 就ack=true
             * @param correlationData 当前消息的唯一关联数据（消息的唯一id）
             * @param ack 消息是否成功
             * @param cause 失败的原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                System.out.println(correlationData+" "+ack+" "+cause);
            }
        });


        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             * 设置消息抵达队列的callback 只要消息没有投递给指定给队列，就触发这个失败回调
             * @param message 投递失败的消息
             * @param replyCode 回复的状态码
             * @param replyText 回复的文本内容
             * @param exchange  当时这个消息发送给哪个交换器
             * @param routingKey 当时这个消息用那个路由键盘
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                System.out.println(message);
                System.out.println(replyCode);
                System.out.println(replyText);
                System.out.println(exchange);
                System.out.println(routingKey);
            }
        });

    }
}
