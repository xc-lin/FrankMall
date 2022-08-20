package com.lxc.frankmall.order.service.impl;

import com.lxc.frankmall.order.entity.OrderReturnReasonEntity;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxc.common.utils.PageUtils;
import com.lxc.common.utils.Query;

import com.lxc.frankmall.order.dao.OrderItemDao;
import com.lxc.frankmall.order.entity.OrderItemEntity;
import com.lxc.frankmall.order.service.OrderItemService;


@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 参数可以写
     * object
     * Message
     * 实体类型
     * Channel 当前传输数据的通道
     *
     * Queue 可以有很多人来监听
     *  只要收到消息，队列删除消息，而且只能又一个人收到此消息
     *
     * @@RabbitListener(queues = {"hello.java.queue"}) 类+方法上 监听哪些队列即可
     * @RabbitHandler 表在方法上，重载区分不同的消息类型
     *
     *
     * @param object
     */
    @RabbitListener(queues = {"hello.java.queue"})
    public void receive(Message message,
                        OrderReturnReasonEntity entity,
                        Channel channel) throws IOException {
        System.out.println("收到消息"+entity);
        // channel内 按顺序自增
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        /**
         * requeue为false 丢弃，
         * equeue=true 发回服务器，服务器重新入队 和没有调用ack方法的效果一样
         *
         * 业务成功调用basicAck
         * 业务失败调用basicNack
         *
         */
        channel.basicNack(deliveryTag,false,true  );
    }


    // @RabbitListener(queues = {"hello.java.queue"})
    public void receive2(OrderReturnReasonEntity entity){
        System.out.println("收到消息222"+entity);
    }

}