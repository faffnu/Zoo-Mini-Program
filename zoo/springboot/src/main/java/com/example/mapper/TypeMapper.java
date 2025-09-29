package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Type;

import java.util.List;

public interface TypeMapper extends BaseMapper<Type> {

    /**
      * 查询所有
    */
    List<Type> selectAll(Type type);

    /**
      * 根据ID查询
    */
    Type selectById(Integer id);

    /**
      * 删除
    */
    int deleteById(Integer id);



}