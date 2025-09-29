package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Ticket;

import java.util.List;

public interface TicketMapper extends BaseMapper<Ticket> {

    /**
      * 查询所有
    */
    List<Ticket> selectAll(Ticket ticket);

    /**
      * 根据ID查询
    */
    Ticket selectById(Integer id);

    /**
      * 删除
    */
    int deleteById(Integer id);



}