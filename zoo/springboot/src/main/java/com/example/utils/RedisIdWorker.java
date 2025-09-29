package com.example.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

//使用Redis来生成唯一ID
//生成的唯一ID由时间戳和序列号两部分组成，时间戳部分保证了ID的有序性，序列号部分保证了同一时间生成的ID的唯一性。
@Component
public class RedisIdWorker {
    //开始时间戳
    private static final long BEGIN_TIMESTAMP=1640995200L;

    //序列号位数
    private static final int COUNT_BITS=32;

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    //生成一个唯一ID
    public Long nextId(String keyPrefix){
        //1.生成时间戳
        LocalDateTime now = LocalDateTime.now();
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        long timestamp = nowSecond - BEGIN_TIMESTAMP;

        //2.生成序列号
        //2.1.获取当前日期，精确到天
        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        String redisKey = "icr:" + keyPrefix + ":" + date;

        // 自增并获取值
        Long count = stringRedisTemplate.opsForValue().increment(redisKey);

        // 设置TTL，确保键在第二天凌晨过期（保留48小时以防跨天问题）
        if (count != null && count == 1) {
            // 只有第一次创建键时设置TTL
            LocalDateTime tomorrow = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            Duration duration = Duration.between(now, tomorrow);
            stringRedisTemplate.expire(redisKey, duration.getSeconds() + 86400, TimeUnit.SECONDS); // 额外增加24小时
        }

        //3.拼接并返回
        return timestamp << COUNT_BITS | count;
    }

}
