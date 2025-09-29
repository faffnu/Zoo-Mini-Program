package com.example.interceptor;

import com.example.utils.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;



public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
       //1.判断是否需要拦截（ThreadLocal）中是否有用户
        if(UserHolder.getUser()==null){
            //没有需要拦截，设置状态码
            response.setStatus(401);
            //拦截
            return false;
        }
        //由用户放行
        return true;
    }
}
