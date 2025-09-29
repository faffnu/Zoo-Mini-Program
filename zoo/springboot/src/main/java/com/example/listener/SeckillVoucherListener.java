package com.example.listener;

import cn.hutool.json.JSONUtil;
import com.example.entity.UserVoucher;
import com.example.service.UserVoucherService;
import com.example.service.VoucherService;
import com.rabbitmq.client.Channel;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class SeckillVoucherListener {

    @Resource
    VoucherService voucherService;
    @Resource
    UserVoucherService userVoucherService;
    /**
     * sheng  消费者1
     * @param message
     * @param channel
     * @throws Exception
     */
    @RabbitListener(queues = "QA")
    public void receivedA(Message message, Channel channel)throws Exception{
        String msg=new String(message.getBody());
        log.info("正常队列:");
        UserVoucher userVoucher = JSONUtil.toBean(msg, UserVoucher.class);
        log.info(userVoucher.toString());
        userVoucherService.add(userVoucher);//保存到数据库
        //数据库秒杀库存减一
        Integer voucherId=userVoucher.getVoucherId();
        voucherService.updateStock(voucherId);
    }

    /**
     * sheng  消费者2
     * @param message
     * @throws Exception
     */
    @RabbitListener(queues = "QD")
    public void receivedD(Message message)throws Exception{
        log.info("死信队列:");
        String msg=new String(message.getBody());
        UserVoucher userVoucher = JSONUtil.toBean(msg, UserVoucher.class);
        log.info(userVoucher.toString());
        userVoucherService.add(userVoucher);//保存到数据库
        //数据库秒杀库存减一
        Integer voucherId=userVoucher.getVoucherId();
        voucherService.updateStock(voucherId);
    }
}
