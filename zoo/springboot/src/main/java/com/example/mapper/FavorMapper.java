package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Favor;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface FavorMapper extends BaseMapper<Favor> {

    /**
      * 查询所有
    */
    List<Favor> selectAll(Favor favor);

    /**
      * 根据ID查询
    */
    Favor selectById(Integer id);

    /**
      * 删除
    */
    int deleteById(Integer id);

    @Select("SELECT animal_id FROM favor WHERE user_id = #{userId}")
    List<Integer> selectAnimalIdsByUser(@Param("userId") int userId);


    void deleteByUserIdAndAnimalId(Integer userId, Integer animalId);

    List<Favor> selectByUserId(@Param("userId") int userId);

    @Select("SELECT count(*) FROM favor WHERE animal_id = #{id}")
    int countByAnimalId(Integer id);
}