package com.example.service;

import com.example.entity.Favor;
import com.example.mapper.FavorMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavorService {

    @Resource
    private FavorMapper favorMapper;

    /**
     * 新增
     */
    public void add(Favor favor) {
        favorMapper.insert(favor);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        favorMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            favorMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Favor favor) {
        favorMapper.updateById(favor);
    }

    /**
     * 根据ID查询
     */
    public Favor selectById(Integer id) {
        return favorMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Favor> selectAll(Favor favor) {
        return favorMapper.selectAll(favor);
    }

    /**
     * 分页查询
     */
    public PageInfo<Favor> selectPage(Favor favor, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<Favor> list = this.selectAll(favor);

        return PageInfo.of(list);
    }
    /**
     * 切换用户收藏状态
     */
    public void favorToggle(Favor favor) {
        List<Integer> list = favorMapper.selectAnimalIdsByUser(favor.getUserId());
        if(list.contains(favor.getAnimalId())){
            favorMapper.deleteByUserIdAndAnimalId(favor.getUserId(), favor.getAnimalId());
        }else {
            favorMapper.insert(favor);
        }

    }

    public List<Favor> selectByUserId(int userId) {
        return favorMapper.selectByUserId(userId);
    }
}