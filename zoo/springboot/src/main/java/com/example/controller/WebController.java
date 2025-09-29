package com.example.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.common.Result;
import com.example.common.UserDTO;
import com.example.common.config.HttpClientUtil;
import com.example.common.enums.ResultCodeEnum;
import com.example.entity.Account;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.service.AdminService;
import com.example.service.UserService;
import com.example.common.enums.RedisConstants;
import com.example.utils.UserHolder;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *  描述：系统用户操作相关接口
 */
@RestController
public class WebController {

	@Resource
	private AdminService adminService;
    @Resource
    private  UserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Value("${wechat.appid}")
    private String appid;

    @Value("${wechat.secret}")
    private String secret;


    /**
     * 描述：用户登录接口
     */
    @PostMapping("/wxLogin")
    public Result wxLogin(@RequestBody LoginRequest request) {
        // 1. 通过code获取openid
        String url = "https://api.weixin.qq.com/sns/jscode2session?" +
                "appid=" + appid +
                "&secret=" + secret +
                "&js_code=" + request.getCode() +
                "&grant_type=authorization_code";

        String response = HttpClientUtil.get(url);
        JSONObject json = JSON.parseObject(response);

        // 2. 验证微信接口返回
        if (json.containsKey("errcode")) {
            throw new RuntimeException("微信认证失败: " + json.getString("errmsg"));
        }

        String openid = json.getString("openid");
        String sessionKey = json.getString("session_key");

        User user = userService.haveUser(openid);
        User user1 = new User();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //是否存在用户,没有就添加,有就判断是否禁用
        if (user==null) {
            user1.setOpenid(openid);
            user1.setAvatar(request.getUserInfo().get("avatarUrl").toString());
            user1.setLastLogin(df.format(new Date()));
            user1.setName(request.getUserInfo().get("nickName").toString());
            userService.add(user1);
            // 生成token
            String token = UUID.randomUUID().toString(true);
            //将user对象转为Hash存储
            UserDTO userDTO = BeanUtil.copyProperties(user1, UserDTO.class);
            Map<String, Object> map = BeanUtil.beanToMap(userDTO, new HashMap<>(),
                    CopyOptions.create().setIgnoreNullValue(true)
                            .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));
            //存储
            String tokenKey= RedisConstants.LOGIN_USER_KEY+token;
            stringRedisTemplate.opsForHash().putAll(tokenKey,map);
            stringRedisTemplate.expire(tokenKey,30, TimeUnit.MINUTES);
            user1.setToken(token);
            return Result.success(user1);
        }else if (user.getIsBanned()=="是"){
            throw new CustomException(ResultCodeEnum.USER_BANNED_ERROR);
        }
        // 更新用户信息
        user.setLastLogin(df.format(new Date()));
        userService.updateById(user);
        // 生成token
        String token = UUID.randomUUID().toString(true);
        //将user对象转为Hash存储
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        Map<String, Object> map = BeanUtil.beanToMap(userDTO, new HashMap<>(),
                CopyOptions.create().setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));
        //存储
        String tokenKey= RedisConstants.LOGIN_USER_KEY+token;
        stringRedisTemplate.opsForHash().putAll(tokenKey,map);
        stringRedisTemplate.expire(tokenKey,30, TimeUnit.MINUTES);
        user.setToken(token);
        return Result.success(user);
    }


    // 请求参数DTO
    @Data
    public static class LoginRequest {
        private String code;
        private Map<String, Object> userInfo;
    }

    /**
     * 描述：管理员登录接口
     */
    @PostMapping("/login")
    public Result login(@RequestBody Account account) {
		if ("admin".equals(account.getRole())) {
            return Result.success(adminService.login(account));
		}
        return Result.error();

    }

    /**
     * 描述：退出登录接口
     */
    @PostMapping("/logout")
    public Result logout(){
        UserHolder.removeUser();
        return Result.success();
    }

    /**
     * 描述：注册接口
     */
    @PostMapping("/register")
    public Result register(@RequestBody Account account) {
		if ("admin".equals(account.getRole())) {
			adminService.register(account);
		}
        return Result.success();
    }

    /**
    * 描述：更新密码接口
    */
    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody Account account) {
		if ("admin".equals(account.getRole())) {
			adminService.updatePassword(account);
		}

        return Result.success();
    }
}
