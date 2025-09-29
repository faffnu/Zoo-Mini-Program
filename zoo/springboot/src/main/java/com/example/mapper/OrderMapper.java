package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Order;

import java.util.List;

public interface OrderMapper extends BaseMapper<Order> {

    /**
      * 查询所有
    */
    List<Order> selectAll(Order order);

    /**
      * 根据ID查询
    */
    Order selectById(Integer id);

    /**
      * 删除
    */
    int deleteById(Integer id);

    /**
      * 添加
    */
    int insert(Order order);

    /**
      * 修改
    */
    int updateById(Order order);


    Order selectByOrderNumber(String orderNumber);
}