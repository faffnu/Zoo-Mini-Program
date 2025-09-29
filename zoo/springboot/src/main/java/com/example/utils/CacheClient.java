package com.example.utils;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.common.enums.RedisConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
/*
* 处理缓存相关的操作
* 实现了两种缓存查询策略：直通式缓存和逻辑过期缓存
* */
@Slf4j
@Component
public class CacheClient {

    private final StringRedisTemplate stringRedisTemplate;


    public CacheClient(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

//将键值对存储到Redis中，并设置过期时间。
    public void set(String key, Object value, Long time, TimeUnit unit){
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value),time,unit);
    }

//设置逻辑过期时间，将数据封装在RedisData对象中，并存储到Redis中
    public void setWithLogicalExpire(String key,Object value,Long time,TimeUnit unit){
        //设置逻辑过期
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));
        //写入redis
        stringRedisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(redisData));
    }

//直通式缓存查询策略。如果缓存中不存在数据，则直接从数据库中查询并返回，同时将查询结果写入缓存。
    public <R,ID> R queryWithPassThrough(
            String keyPrefix, ID id, Class<R> type, Function<ID,R> dbFallback,Long time,TimeUnit unit){
        String key=keyPrefix+id;
        //1.尝试从Redis查询商铺缓存
        String json = stringRedisTemplate.opsForValue().get(key);
        //2.判断缓存是否存在
        if(StrUtil.isNotBlank(json)) { //判断字符串既不为null，也不是空字符串(""),且也不是空白字符
            //3.存在，返回商铺信息
            return JSONUtil.toBean(json, type);

        }
        // 3.判断是否命中空缓存
        if ("".equals(json)) {
            return null;
        }
        //4.不存在，根据id查询数据库
        R r = dbFallback.apply(id);
        //5.判断数据库中是否存在
        if(r==null){
            //6.不存在，返回错误状态码
            stringRedisTemplate.opsForValue().set(key,"", RedisConstants.CACHE_NULL_TTL,TimeUnit.MINUTES);
            return null;
        }
        //7.存在，写入redis，返回商铺信息
       this.set(key,r,time,unit);

        return r;

    }


    private static final ExecutorService CACHE_REBUILD_EXECUTOR= Executors.newFixedThreadPool(10);
    //逻辑过期缓存查询策略。如果缓存中存在数据但已过期，则尝试获取互斥锁，成功后开启独立线程重建缓存。
    public <R,ID> R queryWithLogicalExpire(
            String keyPrefix,ID id,Class<R> type,Function<ID,R> dbFallback,Long time,TimeUnit unit){
        String key=keyPrefix+id;
        //1.尝试从Redis查询缓存
        String json = stringRedisTemplate.opsForValue().get(key);
        //2.判断缓存是否存在
        if(StrUtil.isBlank(json)) { //判断字符串既不为null，也不是空字符串(""),且也不是空白字符
            //3.不存在，返回信息
            return null;
        }

        //4.存在，将json反序列化为对象
        RedisData redisData = JSONUtil.toBean(json, RedisData.class);
        R shop = JSONUtil.toBean((JSONObject) redisData.getData(),type);
        LocalDateTime expireTime = redisData.getExpireTime();
        //5.判断是否过期
        if(expireTime.isAfter(LocalDateTime.now())) {
            //5.1.未过期，直接返回店铺信息
            return shop;
        }
        //5.2.已过期，需要返回缓存重建
        //6.缓存重建
        //6.1.获取互斥锁
        String lockKey=RedisConstants.LOCK_KEY+id;
        boolean isLock = tryLock(lockKey);
        //6.2.判断是否获取锁成功
        if(isLock){
            //  6.3.成功，开启独立线程实现缓存重建
            CACHE_REBUILD_EXECUTOR.submit(()->{
                try {
                   //查询数据库
                    R r1= dbFallback.apply(id);
                    if (r1 != null) {//写入redis
                        this.setWithLogicalExpire(key, r1, time, unit);
                    } else {
                        this.setWithLogicalExpire(key, "", time, unit);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }finally {
                    //释放锁
                    unLock(lockKey);
                }
            });

        }
        //6.4.返回过期的信息
        return shop;
    }
    /**
     * 创建锁
     * @param key
     * @return
     */
    private boolean tryLock(String key){
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }

    /**
     * 封闭锁/释放锁
     * @param key
     */
    private void unLock(String key){
        stringRedisTemplate.delete(key);
    }


    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public Boolean delete(String key) {
        return stringRedisTemplate.delete(key);
    }

    public Long delete(Collection<String> keys) {
        return stringRedisTemplate.delete(keys);
    }

    public Set<String> keys(String pattern) {
        return stringRedisTemplate.keys(pattern);
    }
}
