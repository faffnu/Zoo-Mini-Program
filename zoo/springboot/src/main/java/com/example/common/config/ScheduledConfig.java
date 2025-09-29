package com.example.common.config;

import com.example.service.UserVoucherService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@Slf4j
public class ScheduledConfig {

    @Resource
    private UserVoucherService userVoucherService;

    /**
     * 每天凌晨0点执行过期优惠券检查
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void checkExpiredVouchersTask() {
        try {
            userVoucherService.checkAndUpdateExpiredVouchers();
        } catch (Exception e) {
            log.error("定时检查过期优惠券任务执行失败", e);
        }
    }
}
