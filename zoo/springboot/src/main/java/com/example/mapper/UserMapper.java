package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.User;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {

    /**
      * 查询所有
    */
    List<User> selectAll(User user);

    /**
      * 根据ID查询
    */
    User selectById(Integer id);


    /**
     * 根据openID查询
     */
    User selectByOpenid(String openid);


    /**
      * 切换禁用
    */
    int isBanById(Integer id);

    /**
      * 禁用
    */
    int banById(Integer id);
    /**
     * 解禁
     */
    int unBanById(Integer id);





}