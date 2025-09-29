package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.CommentApprove;

import java.util.List;

public interface CommentApproveMapper extends BaseMapper<CommentApprove> {

    /**
      * 查询所有
    */
    List<CommentApprove> selectAll(CommentApprove commentApprove);

    /**
      * 根据ID查询
    */
    CommentApprove selectById(Integer id);

    /**
      * 删除
    */
    int deleteById(Integer id);


    List<CommentApprove> selectCommentId(Integer commentId);

    void deleteApprove(Integer userId, Integer commentId);
}