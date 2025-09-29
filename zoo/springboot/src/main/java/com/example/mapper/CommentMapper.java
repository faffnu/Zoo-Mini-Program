package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Comment;

import java.util.List;

public interface CommentMapper extends BaseMapper<Comment> {

    /**
      * 查询所有
    */
    List<Comment> selectAll(Comment comment);

    /**
      * 根据ID查询
    */
    Comment selectById(Integer id);

    /**
      * 删除
    */
    int deleteById(Integer id);

    /**
     * 按点赞数排序
     */
    List<Comment> getCommentsOrderByApprove();

    /**
     * 按日期排序
     */
    List<Comment> getCommentsOrderByDate();
}