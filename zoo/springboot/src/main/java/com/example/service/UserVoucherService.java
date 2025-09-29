package com.example.service;

import cn.hutool.json.JSONUtil;
import com.example.common.Result;
import com.example.entity.UserVoucher;
import com.example.mapper.UserVoucherMapper;
import com.example.utils.CacheClient;
import com.example.common.enums.RedisConstants;
import com.example.utils.RedisIdWorker;
import com.example.utils.UserHolder;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserVoucherService {

    @Resource
    private UserVoucherMapper userVoucherMapper;
    @Resource
    private CacheClient cacheClient;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private RedisIdWorker redisIdWorker;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 脚本初始化
     */
    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;
    static {
        SECKILL_SCRIPT=new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }



    private static final String CACHE_KEY = RedisConstants.CACHE_USERVOUCHER_KEY;
    private static final Long LOGICAL_EXPIRE_TTL = RedisConstants.CACHE_LONG_TTL;
    /**
     * 新增
     */
    public void add(UserVoucher userVoucher) {
        userVoucherMapper.insert(userVoucher);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        int deleted = userVoucherMapper.deleteById(id);
        if (deleted > 0) {
            // 删除成功，需要删除缓存
            String key = CACHE_KEY + id;
            cacheClient.delete(key);
        }
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        int deleted =  userVoucherMapper.deleteBatchIds(ids);
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
    public void updateById(UserVoucher userVoucher) {
        int updated = userVoucherMapper.updateById(userVoucher);
        if (updated > 0) {
            // 更新成功，需要删除缓存，保证数据一致性
            // 下次查询时会重新加载最新数据并缓存
            String key = CACHE_KEY + userVoucher.getId();
            cacheClient.delete(key);
        }
    }

    /**
     * 根据ID查询
     */
    public UserVoucher selectById(Long id) {
        return cacheClient.queryWithLogicalExpire(
                CACHE_KEY,
                id,
                UserVoucher.class,
                userVoucherId->userVoucherMapper.selectById(userVoucherId),
                LOGICAL_EXPIRE_TTL,
                TimeUnit.MINUTES
        );
    }

    /**
     * 查询所有
     */
    public List<UserVoucher> selectAll(UserVoucher userVoucher) {
        return userVoucherMapper.selectAll(userVoucher);
    }

    /**
     * 分页查询
     */
    public PageInfo<UserVoucher> selectPage(UserVoucher userVoucher, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<UserVoucher> list = this.selectAll(userVoucher);

        return PageInfo.of(list);
    }

    /**
     * 获取用户可使用的优惠券
     */
    public List<UserVoucher> getUsableVouchers(Integer userId) {
        return userVoucherMapper.selectUsableVouchers(userId);
    }

    /**
     * 获取用户所有的优惠券
     */
    public List<UserVoucher> getAllUserVouchers(Integer userId) {
        return userVoucherMapper.selectAllUserVouchers(userId);
    }

    /**
     * 用户领取优惠券
     */
    public Result receiveVoucher(Integer voucherId) {
        //获取用户id
        Integer userId = UserHolder.getUser().getId();
        //获取用户优惠券id
        long userVoucherId = redisIdWorker.nextId("userVoucher");
        //1.执行lua脚本
        Long result = stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(),
                voucherId.toString(), userId.toString(),String.valueOf(userVoucherId)
        );
        //2.判断结果是否为0
        int r = 0;
        if (result != null) {
            r = result.intValue();
        }
        if(r!=0){
            //2.1.不为0，代表没有购买资格
            return Result.error(r==1?"库存不足":"不能重复领取");
        }
        // 3. 脱离请求线程，发消息给 RabbitMQ
        UserVoucher userVoucher = new UserVoucher();
        userVoucher.setId(userVoucherId);
        userVoucher.setUserId(userId);
        userVoucher.setVoucherId(voucherId);
        userVoucher.setState(0);
        // 你可以用 JSON，也可以用序列化
        // 增加消息发送的异常处理
        //放入mq
        String jsonStr = JSONUtil.toJsonStr(userVoucher);
        try {
            rabbitTemplate.convertAndSend("X","XA",jsonStr );
        } catch (Exception e) {
            log.error("发送 RabbitMQ 消息失败，订单ID: {}", userVoucherId, e);
            throw new RuntimeException("发送消息失败");
        }
        // 4. 返回订单号给前端（实际下单异步处理）
        return Result.success(userVoucherId);
    }



    /**
     * 用户使用优惠券
     */
    @Transactional
    public boolean useVoucher(Long userVoucherId, Integer orderId) {
        UserVoucher userVoucher = userVoucherMapper.selectById(userVoucherId);
        System.out.println(userVoucherId);
        if (userVoucher == null || userVoucher.getState() != 0) {
            return false;
        }
        // 更新优惠券状态为已使用
        userVoucher.setState(1);
        userVoucher.setUseTime(LocalDateTime.now());
        userVoucher.setOrderId(orderId);

        return userVoucherMapper.updateById(userVoucher) > 0;
    }

    /**
     * 检查及更新优惠券有效期
     */
    public void checkAndUpdateExpiredVouchers() {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        // 1. 查询所有未使用且已过期的用户优惠券
        List<UserVoucher> expiredVouchers = userVoucherMapper.selectExpiredVouchers(now);
        if (expiredVouchers.isEmpty()) {
            return;
        }
        // 2. 批量更新状态为已过期
        List<Long> expiredIds = expiredVouchers.stream()
                .map(UserVoucher::getId)
                .collect(Collectors.toList());
        userVoucherMapper.batchUpdateExpiredState(expiredIds, now);
    }
}

