package com.example.common.config;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

//使用Spring Boot和RabbitMQ的配置类，用于定义和绑定交换机（Exchange）和队列（Queue）。
// 它实现了死信队列的功能，即当消息在队列中过期或被拒绝时，将消息发送到另一个队列中。
@Configuration
public class QueueConfig {

    //普通交换机名称
    public static final String X_EXCHANGE="X";
    //死信交换机名称
    public static final String Y_DEAD_LETTER_EXCHANGE="Y";
    //普通队列名称
    public static final String QUEUE_A="QA";
    //死信队列名称
    public static final String DEAD_LETTER_QUEUE_D="QD";

    // 订单超时相关配置
    public static final String ORDER_DELAY_EXCHANGE = "order.delay.exchange";
    public static final String ORDER_DEAD_LETTER_EXCHANGE = "order.dead.letter.exchange";
    public static final String ORDER_DELAY_QUEUE = "order.delay.queue";
    public static final String ORDER_DEAD_LETTER_QUEUE = "order.dead.letter.queue";
    public static final String ORDER_DELAY_ROUTING_KEY = "order.delay.routingkey";
    public static final String ORDER_DEAD_LETTER_ROUTING_KEY = "order.dead.letter.routingkey";

    /**
     * 订单延迟交换机（普通交换机）
     */
    @Bean("orderDelayExchange")
    public DirectExchange orderDelayExchange() {
        return new DirectExchange(ORDER_DELAY_EXCHANGE);
    }

    /**
     * 订单死信交换机
     */
    @Bean("orderDeadLetterExchange")
    public DirectExchange orderDeadLetterExchange() {
        return new DirectExchange(ORDER_DEAD_LETTER_EXCHANGE);
    }

    /**
     * 订单延迟队列（普通队列），设置死信交换机和路由键
     */
    @Bean("orderDelayQueue")
    public Queue orderDelayQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", ORDER_DEAD_LETTER_EXCHANGE);
        args.put("x-dead-letter-routing-key", ORDER_DEAD_LETTER_ROUTING_KEY);
        // 设置队列中所有消息的TTL为1小时（3600000毫秒）
        args.put("x-message-ttl", 3600000);
        return QueueBuilder.durable(ORDER_DELAY_QUEUE).withArguments(args).build();
    }

    /**
     * 订单死信队列
     */
    @Bean("orderDeadLetterQueue")
    public Queue orderDeadLetterQueue() {
        return QueueBuilder.durable(ORDER_DEAD_LETTER_QUEUE).build();
    }

    /**
     * 绑定订单延迟队列到订单延迟交换机
     */
    @Bean
    public Binding orderDelayQueueBinding(@Qualifier("orderDelayQueue") Queue orderDelayQueue,
                                          @Qualifier("orderDelayExchange") DirectExchange orderDelayExchange) {
        return BindingBuilder.bind(orderDelayQueue).to(orderDelayExchange).with(ORDER_DELAY_ROUTING_KEY);
    }

    /**
     * 绑定订单死信队列到订单死信交换机
     */
    @Bean
    public Binding orderDeadLetterQueueBinding(@Qualifier("orderDeadLetterQueue") Queue orderDeadLetterQueue,
                                               @Qualifier("orderDeadLetterExchange") DirectExchange orderDeadLetterExchange) {
        return BindingBuilder.bind(orderDeadLetterQueue).to(orderDeadLetterExchange).with(ORDER_DEAD_LETTER_ROUTING_KEY);
    }


    /**
     * 声明x交换机
     * @return
     */
    @Bean("xExchange")//别名和方法名取一样
    public DirectExchange xExchange(){
        return new DirectExchange(X_EXCHANGE);
    }

    /**
     * 声明y交换机
     * @return
     */
    @Bean("yExchange")//别名和方法名取一样
    public DirectExchange yExchange(){
        return new DirectExchange(Y_DEAD_LETTER_EXCHANGE);
    }

    /**
     * 声明队列A
     * @return
     */
    @Bean("queueA")
    public Queue queueA(){
        final HashMap<String, Object> arguments
                = new HashMap<>();
        //设置死信交换机
        arguments.put("x-dead-letter-exchange",Y_DEAD_LETTER_EXCHANGE);
        //设置死信RoutingKey
        arguments.put("x-dead-letter-routing-key","YD");
        //设置TTL设置10秒过期
        arguments.put("x-message-ttl",10000);

        return QueueBuilder.durable(QUEUE_A)
                .withArguments(arguments)
                .build();
    }

    /**
     * 声明死信队列D
     * @return
     */
    @Bean("queueD")
    public Queue queueD(){
        return QueueBuilder.durable(DEAD_LETTER_QUEUE_D)
                .build();
    }

    /**
     * A队列绑定X交换机
     * @param queueA
     * @return
     */
    @Bean
    public Binding queueABindingX(@Qualifier("queueA")Queue queueA,
                                  @Qualifier("xExchange") DirectExchange xExchange){
        return BindingBuilder.bind(queueA).to(xExchange).with("XA");
    }

    /**
     * d队列绑定y交换机
     * @param queueD
     * @return
     */
    @Bean
    public  Binding queueDBindingY(@Qualifier("queueD")Queue queueD,
                                   @Qualifier("yExchange") DirectExchange yExchange
    ){
        return BindingBuilder.bind(queueD).to(yExchange).with("YD");
    }


}

