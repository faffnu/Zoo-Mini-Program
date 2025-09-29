package com.example.service;

import com.example.entity.Voucher;
import com.example.mapper.VoucherMapper;
import com.example.utils.CacheClient;
import com.example.common.enums.RedisConstants;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoucherService {

    @Resource
    private VoucherMapper voucherMapper;
    @Resource
    private CacheClient cacheClient;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final String CACHE_KEY = RedisConstants.CACHE_VOUCHER_KEY;
    private static final Long LOGICAL_EXPIRE_TTL = RedisConstants.CACHE_LONG_TTL;
    /**
     * 新增
     */
    public void add(Voucher voucher) {
        voucherMapper.insert(voucher);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        int deleted = voucherMapper.deleteById(id);
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
        int deleted =  voucherMapper.deleteBatchIds(ids);
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
    public void updateById(Voucher voucher) {
      voucherMapper.updateById(voucher);
    }

    /**
     * 根据ID查询
     */
    public Voucher selectById(Integer id) {
        return cacheClient.queryWithLogicalExpire(
                CACHE_KEY,
                id,
                Voucher.class,
                voucherId->voucherMapper.selectById(voucherId),
                LOGICAL_EXPIRE_TTL,
                TimeUnit.MINUTES
        );
    }

    /**
     * 查询所有
     */
    public List<Voucher> selectAll(Voucher voucher) {
        return voucherMapper.selectAll(voucher);
    }

    /**
     * 分页查询
     */
    public PageInfo<Voucher> selectPage(Voucher voucher, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<Voucher> list = this.selectAll(voucher);

        return PageInfo.of(list);
    }
    /**
     * 查询可领取的优惠券
     */
    public List<Voucher> selectAvailable() {
        // 1. 从数据库查询有效的优惠券列表
        List<Voucher> voucherList = voucherMapper.selectAvailable();
        // 2. 遍历并同步到Redis
        for (Voucher voucher : voucherList) {
            String stockKey = "seckill:stock:" + voucher.getId();
            // 3. 计算过期时间（秒）
            long ttlSeconds = calculateTTL(voucher.getBeginTime(), voucher.getEndTime());
            // 4. 存入Redis并设置过期时间
            stringRedisTemplate
                    .opsForValue().set(
                            stockKey,
                            String.valueOf(voucher.getStock()),
                            ttlSeconds,
                            TimeUnit.SECONDS
                    );
        }
        return voucherList;
    }


    public void updateStock(Integer id) {
        voucherMapper.updateStock(id);
    }


    /**
     * 计算Redis key的有效时间（秒）
     * 如果优惠券已经开始，则按剩余结束时间计算
     * 如果优惠券还未开始，则按完整有效期计算
     */
    private long calculateTTL(LocalDateTime beginTime, LocalDateTime endTime) {
        LocalDateTime now = LocalDateTime.now();
        // 如果优惠券已经结束，返回0（不应该发生，因为SQL已经过滤）
        if (now.isAfter(endTime)) {
            return 0;
        }
        // 如果优惠券已经开始，按剩余时间计算
        if (now.isAfter(beginTime) || now.isEqual(beginTime)) {
            return Duration.between(now, endTime).getSeconds();
        }
        // 如果优惠券还未开始，按完整有效期计算
        return Duration.between(beginTime, endTime).getSeconds();
    }

    /**
     * 同步单个优惠券的库存（用于优惠券创建或更新时）
     */
    public void syncSingleVoucherStock(Integer voucherId) {
        Voucher voucher = voucherMapper.selectById(voucherId);
        if (voucher != null && voucher.getStock() > 0 && voucher.getEndTime().isAfter(LocalDateTime.now())) {
            String stockKey = "seckill:stock:" + voucherId;
            long ttlSeconds = calculateTTL(voucher.getBeginTime(), voucher.getEndTime());
            stringRedisTemplate
                    .opsForValue().set(
                            stockKey,
                            String.valueOf(voucher.getStock()),
                            ttlSeconds,
                            TimeUnit.SECONDS
                    );
        }
    }
}
