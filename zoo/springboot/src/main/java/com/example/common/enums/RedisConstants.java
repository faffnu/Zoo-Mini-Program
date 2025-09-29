package com.example.common.enums;

//静态常量
public class RedisConstants {

    public static final String CACHE_ACTIVITY_KEY = "cache:activity:";
    public static final String CACHE_ANIMAL_KEY = "cache:animal:";
    public static final String CACHE_COMMENT_KEY = "cache:comment:";
    public static final String CACHE_ORDER_KEY = "cache:order:";
    public static final String CACHE_USER_KEY = "cache:user:";
    public static final String CACHE_TICKET_KEY = "cache:ticket:";
    public static final String CACHE_TYPE_KEY = "cache:type:";
    public static final String CACHE_VOUCHER_KEY = "cache:voucher:";
    public static final String CACHE_USERVOUCHER_KEY = "cache:uservoucher:";

    public static final Long CACHE_NULL_TTL = 2L;
    public static final Long CACHE_SHORT_TTL = 5L; // 5分钟，用于订单、用户等
    public static final Long CACHE_MID_TTL = 30L; // 30分钟
    public static final Long CACHE_LONG_TTL = 24 * 60L; // 24小时，用于逻辑过期

    public static final String LOCK_KEY = "lock:";

    public static final String LOGIN_USER_KEY = "login:token:";
    public static final Long LOGIN_USER_TTL = 36000L;



    public static final String ANIMAL_GEO_KEY = "animal:geo";

    public static final String SECKILL_STOCK_KEY = "seckill:stock:";
    public static final String BLOG_LIKED_KEY = "blog:liked:";
    public static final String FEED_KEY = "feed:";



}
