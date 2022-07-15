package com.lxc.frankmall.order.listen;

import com.lxc.frankmall.order.entity.OrderEntity;
import com.lxc.frankmall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author Frank_lin
 * @date 2022/7/6
 */
@Service
@RabbitListener(queues = {"order.release.order.queue"})
public class OrderCloseListener {

    @Autowired
    OrderService orderService;

    @RabbitHandler
    public void listen(OrderEntity entity, Channel channel, Message message) throws IOException {
        System.out.println("收到过期的订单信息 " + entity.getOrderSn());
        try {
            orderService.closeOrder(entity);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);

        }
    }

}
