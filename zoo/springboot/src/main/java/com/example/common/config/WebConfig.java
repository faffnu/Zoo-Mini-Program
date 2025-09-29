package com.example.common.config;

import com.example.interceptor.LoginInterceptor;
import com.example.interceptor.RefreshTokenInterceptor;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // 加自定义拦截器JwtInterceptor，设置拦截规则
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //登录拦截器
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns("/")
                .excludePathPatterns("/login","/wxLogin", "/register", "/files/**",
                        "/ticket/selectAll","/ticket/selectById/**","/comment/**",
                        "/animal/selectAll","/animal/nearby","/animal/selectById/**","/animal/selectPage",
                        "/activity/selectAll","/activity/selectById/**",
                        "/type/selectAll","/commentApprove/selectAll",
                        //"/userVoucher/**","/voucher/**",
                        "/recommend/popular","/alipay/**").order(1);
        //token刷新的拦截器
        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate))
                .addPathPatterns("/**").order(0);

    }
}