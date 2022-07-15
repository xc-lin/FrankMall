package com.lxc.frankmall.order;

import com.lxc.frankmall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.UUID;

@Slf4j
@SpringBootTest
class FrankMallOrderApplicationTests {


    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 如何创建exchange[hello.java.exchange] queue binding
     * 如何收发消息
     */
    @Test
    void contextLoads() {
        // Exchange
        amqpAdmin.declareExchange(new DirectExchange("hello.java.exchange", true, false, null));
        log.info("exchange 创建成功");
    }

    @Test
    void createQueue() {
        amqpAdmin.declareQueue(new Queue("hello.java.queue",true,false, false,null));

        log.info("queue 创建成功");
    }

    @Test
    void createBinding() {
        amqpAdmin.declareBinding(new Binding("hello.java.queue",
                Binding.DestinationType.QUEUE,
                "hello.java.exchange",
                "hello.java",
                null));

        log.info("binding 创建成功");
    }

    @Test
    void sendMessageTest() {

        OrderReturnReasonEntity orderReturnReasonEntity = new OrderReturnReasonEntity();
        orderReturnReasonEntity.setId(1L);
        orderReturnReasonEntity.setName("haha");
        orderReturnReasonEntity.setCreateTime(new Date());
        // 如果发送的消息是一个对象，会使用序列化机制，将对象写出去，对象必须实现Serializable接口
        // 发送的对  象消息是json
        rabbitTemplate.convertAndSend("hello.java.exchange","hello.java",orderReturnReasonEntity,new CorrelationData(UUID.randomUUID().toString()));
    }
}
