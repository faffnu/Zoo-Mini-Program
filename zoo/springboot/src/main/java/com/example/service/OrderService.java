package com.example.service;


import com.example.common.config.QueueConfig;
import com.example.entity.Order;
import com.example.entity.Ticket;
import com.example.entity.Voucher;
import com.example.mapper.OrderMapper;
import com.example.mapper.TicketMapper;
import com.example.mapper.VoucherMapper;
import com.example.utils.CacheClient;
import com.example.common.enums.RedisConstants;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private TicketMapper ticketMapper;
    @Resource
    private VoucherMapper voucherMapper;
    @Resource
    private CacheClient cacheClient;
    @Resource
    private RabbitTemplate rabbitTemplate;

    private static final String CACHE_KEY = RedisConstants.CACHE_ORDER_KEY;

    public Order getByOrderNumber(String orderNumber) {
        return orderMapper.selectByOrderNumber(orderNumber);
    }

    // 用于生成订单号的辅助类，确保线程安全
    private static class OrderNumberGenerator {
        private static final AtomicInteger counter = new AtomicInteger(0);
        private static final Random random = new Random();
        private static final String PREFIX = "ORD";

        public static synchronized String generateOrderNumber() {
            int counterValue = counter.incrementAndGet();
            int randomValue = random.nextInt(999999); // 生成一个六位的随机数
            return PREFIX + String.format("%06d%06d", counterValue, randomValue);
        }
    }

    /**
     * 新增
     *
     * @return
     */
    public Order add(Order order) {
        // 生成随机订单号
        String orderNumber = OrderNumberGenerator.generateOrderNumber();
        order.setOrderNumber(orderNumber);

        // 设置订单状态为待支付
        order.setState("待支付");

        // 设置下单时间为当前时间
        order.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        // 计算订单总价
        Ticket ticket = ticketMapper.selectById(order.getTicketId());
        if (ticket != null) {
            double totalPrice = ticket.getPrice() * order.getNumber();
            double discountAmount = 0.00;
            // 如果使用了优惠券，计算优惠金额
            if (order.getVoucherId() != null) {
                Voucher voucher = voucherMapper.selectById(order.getVoucherId());
                if (voucher != null) {
                    // 检查优惠券是否在有效期内
                    LocalDateTime now = LocalDateTime.now();
                    if (now.isBefore(voucher.getBeginTime()) || now.isAfter(voucher.getEndTime())) {
                        throw new IllegalArgumentException("优惠券不在有效期内");
                    }
                    // 根据优惠券类型计算优惠金额
                    if (voucher.getType() == 1) { // 折扣券
                        // 折扣券：value为折扣率（如0.9表示9折）
                        if (voucher.getValue() <= 0 || voucher.getValue() > 1) {
                            throw new IllegalArgumentException("折扣券的折扣率必须在0-1之间");
                        }
                        discountAmount = totalPrice * (1 - voucher.getValue());
                        totalPrice = totalPrice * voucher.getValue();
                    } else if (voucher.getType() == 2) { // 满减券
                        // 满减券：value为减免金额，需要满足最低消费金额
                        if (totalPrice < voucher.getMinAmount()) {
                            throw new IllegalArgumentException("订单金额不满足满减券使用条件，最低消费: " + voucher.getMinAmount());
                        }
                        discountAmount = voucher.getValue();
                        totalPrice = totalPrice - voucher.getValue();

                        // 确保优惠金额不超过订单总价
                        if (discountAmount > totalPrice) {
                            discountAmount = totalPrice;
                        }

                    }
                } else {
                    throw new IllegalArgumentException("无效的优惠券ID: " + order.getVoucherId());
                }
            }
            // 设置最终价格和优惠金额
            order.setTotalPrice(totalPrice);
            order.setDiscountAmount(discountAmount);

        } else {
            throw new IllegalArgumentException("无效的门票ID: " + order.getTicketId());
        }
        // 插入订单记录
        orderMapper.insert(order);
        Order order1 = orderMapper.selectByOrderNumber(orderNumber);
        // 发送订单超时消息到RabbitMQ
        if (order1 != null) {
            try {
                rabbitTemplate.convertAndSend(
                        QueueConfig.ORDER_DELAY_EXCHANGE,
                        QueueConfig.ORDER_DELAY_ROUTING_KEY,
                        order1.getId().toString()
                );
                log.info("已发送订单超时消息，订单ID: {}", order1.getId());
            } catch (Exception e) {
                log.error("发送订单超时消息失败，订单ID: {}", order1.getId(), e);
            }
        }
        return order1;

    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        int deleted = orderMapper.deleteById(id);
        if (deleted > 0) {
            String key = CACHE_KEY + id;
            cacheClient.delete(key);
        }
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        int deleted =  orderMapper.deleteBatchIds(ids);
        if (deleted > 0) {
            // 删除成功，需要删除缓存
            List<String> keys = ids.stream()
                    .map(id -> CACHE_KEY + id)
                    .collect(Collectors.toList());
            cacheClient.delete(keys);
        }
    }

    /**
     * 修改
     */
    public void updateById(Order order) {
        int updated = orderMapper.updateById(order);
        if (updated > 0) {
            // 订单状态等重要信息更新，必须清除缓存，保证下次读取最新状态
            String key = CACHE_KEY + order.getId();
            cacheClient.delete(key);
        }
    }

    /**
     * 根据ID查询
     */
    public Order selectById(Integer id) {

        return cacheClient.queryWithPassThrough(
                CACHE_KEY,
                id,
                Order.class,
                orderId->orderMapper.selectById(orderId),
                RedisConstants.CACHE_SHORT_TTL,
                TimeUnit.MINUTES
        );
    }

    /**
     * 查询所有
     */
    public List<Order> selectAll(Order order) {
        return orderMapper.selectAll(order);
    }

    /**
     * 分页查询
     */
    public PageInfo<Order> selectPage(Order order, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<Order> list = this.selectAll(order);

        return PageInfo.of(list);
    }

}