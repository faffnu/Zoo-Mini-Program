package com.example.service;

import com.example.entity.Comment;
import com.example.entity.CommentApprove;
import com.example.mapper.CommentApproveMapper;
import com.example.mapper.CommentMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentApproveService {

    @Resource
    private CommentApproveMapper commentApproveMapper;
    @Resource
    private CommentMapper commentMapper;


    /**
     * 新增
     */
    public void add(CommentApprove commentApprove) {
        commentApproveMapper.insert(commentApprove);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        commentApproveMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            commentApproveMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(CommentApprove commentApprove) {
        commentApproveMapper.updateById(commentApprove);
    }

    /**
     * 根据ID查询
     */
    public CommentApprove selectById(Integer id) {
        return commentApproveMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<CommentApprove> selectAll(CommentApprove commentApprove) {
        return commentApproveMapper.selectAll(commentApprove);
    }

    /**
     * 分页查询
     */
    public PageInfo<CommentApprove> selectPage(CommentApprove commentApprove, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<CommentApprove> list = this.selectAll(commentApprove);

        return PageInfo.of(list);
    }

    public void approveToggle(CommentApprove commentApprove) {
        List<CommentApprove> list = commentApproveMapper.selectAll(commentApprove);
        Comment comment = commentMapper.selectById(commentApprove.getCommentId());
        if(list.size()!=0) {// 如果存在，则删除
            commentApproveMapper.deleteApprove(commentApprove.getUserId(),commentApprove.getCommentId());
            comment.setApprove(comment.getApprove() - 1);
        }else {
            commentApproveMapper.insert(commentApprove);
            comment.setApprove(comment.getApprove() + 1);
        }
        commentMapper.updateById(comment);
    }
}