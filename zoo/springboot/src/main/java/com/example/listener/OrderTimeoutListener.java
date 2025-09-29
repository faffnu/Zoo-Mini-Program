package com.example.listener;

import com.example.entity.Order;
import com.example.service.OrderService;
import com.rabbitmq.client.Channel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class OrderTimeoutListener {

    @Resource
    private OrderService orderService;

    /**
     * 监听订单超时死信队列
     */
    @RabbitListener(queues = "order.dead.letter.queue")
    public void handleOrderTimeout(String orderId, Channel channel, Message message) throws IOException {
        log.info("收到订单超时消息，订单ID: {}", orderId);

        try {
            // 根据订单ID查询订单
            Order order = orderService.selectById(Integer.parseInt(orderId));

            if (order != null && "待支付".equals(order.getState())) {
                // 如果订单状态仍是待支付，则取消订单
                order.setState("已取消");
                orderService.updateById(order);
                log.info("订单超时取消，订单ID: {}", orderId);
            } else {
                log.info("订单已支付或已处理，无需取消，订单ID: {}", orderId);
            }

            // 手动确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("处理订单超时消息失败，订单ID: {}", orderId, e);
            // 处理失败，拒绝消息，并重新放回队列
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }
}
