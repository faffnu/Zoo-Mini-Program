package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Animal;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AnimalMapper extends BaseMapper<Animal> {

    /**
      * 查询所有
    */
    List<Animal> selectAll(Animal animal);

    /**
      * 根据ID查询
    */
    Animal selectById(Integer id);

    /**
      * 删除
    */
    int deleteById(Integer id);


    @Select("SELECT DISTINCT animal_id FROM favor UNION SELECT DISTINCT animal_id FROM look")
    List<Integer> selectAllAnimalIds();


    List<Integer> selectPopularAnimalIds(@Param("limit") int limit);


    List<Animal> selectByIds(List<Integer> ids);

    List<Animal> selectBatchIdsWithCondition(@Param("ids") List<Long> ids, @Param("animal") Animal animal);

}