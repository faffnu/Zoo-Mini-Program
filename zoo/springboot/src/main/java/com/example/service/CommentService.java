package com.example.service;

import com.example.entity.Comment;
import com.example.mapper.CommentMapper;
import com.example.utils.CacheClient;
import com.example.common.enums.RedisConstants;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    @Resource
    private CommentMapper commentMapper;
    @Resource
    private final CacheClient cacheClient;

    private static final String CACHE_ITEM_KEY = RedisConstants.CACHE_COMMENT_KEY; // 单个缓存
    private static final Long CACHE_ITEM_TTL = RedisConstants.CACHE_SHORT_TTL;

    /**
     * 新增
     */
    public void add(Comment comment) {
        comment.setApprove(0);
        comment.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        commentMapper.insert(comment);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        int del = commentMapper.deleteById(id);
        if (del > 0) {
            String key = CACHE_ITEM_KEY + id;
            cacheClient.delete(key);
        }
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        int deleted =  commentMapper.deleteBatchIds(ids);
        if (deleted > 0) {
            // 删除成功，需要删除缓存
            List<String> keys = ids.stream()
                    .map(id -> CACHE_ITEM_KEY + id)
                    .collect(Collectors.toList());
            cacheClient.delete(keys);
        }
    }

    /**
     * 修改
     */
    public void updateById(Comment comment) {
        int updated = commentMapper.updateById(comment);
        if (updated > 0) {
            // 更新成功，需要删除缓存，保证数据一致性
            // 下次查询时会重新加载最新数据并缓存
            String key = CACHE_ITEM_KEY + comment.getId();
            cacheClient.delete(key);
        }
    }

    /**
     * 根据ID查询
     */
    public Comment selectById(Integer id) {
        // 使用直通式缓存，设置较短时间，因为评论可能被更新（点赞数变化）
        return cacheClient.queryWithPassThrough(
                CACHE_ITEM_KEY,
                id,
                Comment.class,
                commentId->commentMapper.selectById(commentId),
                RedisConstants.CACHE_SHORT_TTL,
                TimeUnit.MINUTES
        );
    }

    /**
     * 查询所有
     */
    public List<Comment> selectAll(Comment comment) {
        return commentMapper.selectAll(comment);
    }

    /**
     * 按点赞数排序
     */
    public List<Comment> getCommentsOrderByApprove(){
        return commentMapper.getCommentsOrderByApprove();
    }

    /**
     * 分页查询
     */
    public PageInfo<Comment> selectPage(Comment comment, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<Comment> list = this.selectAll(comment);
        return PageInfo.of(list);
    }

    public List<Comment> getCommentsOrderByDate() {
        return commentMapper.getCommentsOrderByDate();
    }

    public float getScore() {
        List<Comment> comments= commentMapper.selectAll(new Comment());
        float total = 0;
        for (int i = 0; i < comments.size(); i++) {
            total+= comments.get(i).getScore();
        }
        float score = ((float)Math.round(total / comments.size()*10))/10;

        return score;
    }
}