package com.example.mapper;

import com.example.entity.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

public interface LookMapper extends BaseMapper<Look> {

    /**
      * 查询所有
    */
    List<Look> selectAll(Look look);

    /**
      * 根据ID查询
    */
    Look selectById(Integer id);

    /**
      * 删除
    */
    int deleteById(Integer id);

    @Select("SELECT animal_id FROM look WHERE user_id = #{userId}")
    List<Integer> selectAnimalIdsByUser(@Param("userId") int userId);

    List<Look> selectByUserId(int userId);
}