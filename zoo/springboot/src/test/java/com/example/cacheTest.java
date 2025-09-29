package com.example;

import com.example.entity.*;
import com.example.mapper.*;
import com.example.utils.CacheClient;
import com.example.common.enums.RedisConstants;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.common.enums.RedisConstants.CACHE_LONG_TTL;

@SpringBootTest
public class cacheTest {
    @Resource
    private AnimalMapper animalMapper;

    @Resource
    private ActivityMapper activityMapper;

    @Resource
    private TypeMapper typeMapper;

    @Resource
    private TicketMapper ticketMapper;

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private CacheClient cacheClient;

    @Resource
    private VoucherMapper voucherMapper;

    @Resource
    private UserVoucherMapper userVoucherMapper;

    //缓存动物信息
    @Test
    public void testCacheAllAnimals() {
        // 1. 从数据库中查询所有动物信息
        List<Animal> animalList = animalMapper.selectAll(new Animal());
        // 2. 遍历动物列表，将每个动物的信息写入Redis
        for (Animal animal : animalList) {
            // 生成Redis键
            String key = RedisConstants.CACHE_ANIMAL_KEY + animal.getId();
            // 将店铺信息写入Redis，并设置逻辑过期时间
            cacheClient.setWithLogicalExpire(key, animal, CACHE_LONG_TTL, TimeUnit.HOURS);
        }
    }



    //缓存活动信息
    @Test
    public void testCacheAllActivities() {
        // 1. 从数据库中查询所有信息
        List<Activity> activityList = activityMapper.selectAll(new Activity());
        // 2. 遍历列表，将每个信息写入Redis
        for (Activity activity : activityList) {
            // 生成Redis键
            String key = RedisConstants.CACHE_ACTIVITY_KEY + activity.getId();
            // 将信息写入Redis，并设置逻辑过期时间
            cacheClient.setWithLogicalExpire(key, activity, RedisConstants.CACHE_SHORT_TTL, TimeUnit.HOURS);
        }
    }

    //缓存门票信息
    @Test
    public void testCacheAllTickets() {
        // 1. 从数据库中查询所有信息
        List<Ticket> ticketList = ticketMapper.selectAll(new Ticket());
        // 2. 遍历列表，将每个信息写入Redis
        for (Ticket ticket : ticketList) {
            // 生成Redis键
            String key = RedisConstants.CACHE_TICKET_KEY + ticket.getId();
            // 将信息写入Redis，并设置逻辑过期时间
            cacheClient.setWithLogicalExpire(key, ticket, RedisConstants.CACHE_LONG_TTL, TimeUnit.HOURS);
        }
    }

    //缓存动物分类信息
    @Test
    public void testCacheAllTypes() {
        // 1. 从数据库中查询所有信息
        List<Type> typeList = typeMapper.selectAll(new Type());
        // 2. 遍历列表，将每个信息写入Redis
        for (Type type : typeList) {
            // 生成Redis键
            String key = RedisConstants.CACHE_TYPE_KEY + type.getId();
            // 将信息写入Redis，并设置逻辑过期时间
            cacheClient.setWithLogicalExpire(key, type, RedisConstants.CACHE_LONG_TTL, TimeUnit.HOURS);
        }
    }

    //缓存评论信息
    @Test
    public void testCacheAllComments() {
        // 1. 从数据库中查询所有信息
        List<Comment> commentList = commentMapper.selectAll(new Comment());
        // 2. 遍历列表，将每个信息写入Redis
        for (Comment comment : commentList) {
            // 生成Redis键
            String key = RedisConstants.CACHE_COMMENT_KEY + comment.getId();
            // 将信息写入Redis，并设置逻辑过期时间
            cacheClient.set(key, comment, RedisConstants.CACHE_SHORT_TTL, TimeUnit.HOURS);
        }
    }

    //缓存优惠券信息
    @Test
    public void testCacheAllVouchers() {
        // 1. 从数据库中查询所有信息
        List<Voucher> voucherList = voucherMapper.selectAll(new Voucher());
        // 2. 遍历列表，将每个信息写入Redis
        for (Voucher voucher : voucherList) {
            // 生成Redis键
            String key = RedisConstants.CACHE_VOUCHER_KEY + voucher.getId();
            // 将信息写入Redis，并设置逻辑过期时间
            cacheClient.setWithLogicalExpire(key, voucher, RedisConstants.CACHE_SHORT_TTL, TimeUnit.HOURS);
        }
    }

    //缓存用户优惠券信息
    @Test
    public void testCacheAllUserVouchers() {
        // 1. 从数据库中查询所有信息
        List<UserVoucher> userVoucherList = userVoucherMapper.selectAll(new UserVoucher());
        // 2. 遍历列表，将每个信息写入Redis
        for (UserVoucher userVoucher : userVoucherList) {
            // 生成Redis键
            String key = RedisConstants.CACHE_USERVOUCHER_KEY + userVoucher.getId();
            // 将信息写入Redis，并设置逻辑过期时间
            cacheClient.setWithLogicalExpire(key, userVoucher, RedisConstants.CACHE_SHORT_TTL, TimeUnit.HOURS);
        }
    }
}
