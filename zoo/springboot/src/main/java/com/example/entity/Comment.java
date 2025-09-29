package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 评论
 */
@Data
@TableName("comment")
public class Comment  {

    /** id */
    @TableId(type = IdType.AUTO)
    private Integer id;
	/** 用户 */
	@TableField(exist = false)
	private String user;
	/** 用户头像 */
	@TableField(exist = false)
	private String avatar;
	/** 评分 */
	private Double score;
	/** 评论内容 */
	private String content;
	/** 图片 */
	private String img;
	/** 体验视频 */
	private String video;
	/** 点赞数 */
	private Integer approve;
	/** 评论时间 */
	private String time;
	/** 用户Id */
	private Integer userId;



}
