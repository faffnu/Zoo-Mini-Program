package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户收藏
 */
@Data
@TableName("favor")
public class Favor  {

    /** id */
    @TableId(type = IdType.AUTO)
    private Integer id;
	/** 用户 */
	@TableField(exist = false)
	private String user;
	/** 动物 */
	@TableField(exist = false)
	private String animal;
	/** 用户Id */
	private Integer userId;
	/** 动物信息Id */
	private Integer animalId;
	/** 动物图片 */
	@TableField(exist = false)
	private String picUrl;



}
