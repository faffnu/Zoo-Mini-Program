package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

/**
 * 活动
 */
@Data
@TableName("activity")
public class Activity  {

    /** id */
    @TableId(type = IdType.AUTO)
    private Integer id;
	/** 活动标题 */
	private String title;
	/** 活动封面图 */
	private String image;
	/** 活动介绍 */
	private String introduce;
	/** 具体内容 */
	private String content;
	/** 活动时间 */
	private String time;
	/** 发布日期 */
	private String publishDate;
	/** 活动时间 */
	@TableField(exist = false)
	private List<String> timeSlot;



}
