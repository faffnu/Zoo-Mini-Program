package com.example.service;

import com.example.entity.Look;
import com.example.mapper.LookMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LookService {

    @Resource
    private LookMapper lookMapper;

    /**
     * 新增
     */
    public void add(Look look) {
        List<Look> looks = selectAll(look);
        if(looks.isEmpty()) {
            lookMapper.insert(look);
        }
    }


    /**
     * 删除
     */
    public void deleteById(Integer id) {
        lookMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            lookMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Look look) {
        lookMapper.updateById(look);
    }

    /**
     * 根据ID查询
     */
    public Look selectById(Integer id) {
        return lookMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Look> selectAll(Look look) {
        return lookMapper.selectAll(look);
    }

    /**
     * 分页查询
     */
    public PageInfo<Look> selectPage(Look look, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<Look> list = this.selectAll(look);

        return PageInfo.of(list);
    }

    public List<Look> selectByUserId(int userId) {
        return lookMapper.selectByUserId(userId);
    }
}