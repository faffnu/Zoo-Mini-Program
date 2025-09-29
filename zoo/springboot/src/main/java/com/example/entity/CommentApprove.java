package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 评论点赞
 */
@Data
@TableName("comment_approve")
public class CommentApprove  {

    /** id */
    @TableId(type = IdType.AUTO)
    private Integer id;
	/** 用户 */
	@TableField(exist = false)
	private String user;
	/** 评论 */
	@TableField(exist = false)
	private String comment;
	/** 用户Id */
	private Integer userId;
	/** 评论Id */
	private Integer commentId;



}
