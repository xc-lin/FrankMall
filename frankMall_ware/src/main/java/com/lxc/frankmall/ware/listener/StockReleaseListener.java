package com.lxc.frankmall.ware.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxc.common.to.OrderTo;
import com.lxc.common.to.mq.StockDetailTo;
import com.lxc.common.to.mq.StockLockedTo;
import com.lxc.common.utils.R;
import com.lxc.frankmall.ware.entity.WareOrderTaskDetailEntity;
import com.lxc.frankmall.ware.entity.WareOrderTaskEntity;
import com.lxc.frankmall.ware.service.WareSkuService;
import com.lxc.frankmall.ware.vo.OrderVo;
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
@RabbitListener(queues = {"stock.release.stock.queue"})
public class StockReleaseListener {

    @Autowired
    WareSkuService wareSkuService;

    @RabbitHandler
    void handleStockLockRelease(StockLockedTo to, Message message, Channel channel) throws IOException {
        System.out.println("收到解锁库存的消息");
        try {
            wareSkuService.unLockStock(to);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }


    }


    @RabbitHandler
    void handleOrderCloseRelease(OrderTo to, Message message, Channel channel) throws IOException {
        System.out.println("收到订单关闭的消息");
        try {
            wareSkuService.unLock(to);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }


    }
}
