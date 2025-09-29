package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户
 */
@Data
@TableName("user")
public class User  {

    /** id */
    @TableId(type = IdType.AUTO)
    private Integer id;
	/** 昵称 */
	private String name;
	/** 头像 */
	private String avatar;
	/** 性别 */
	private String gender;
	/** 手机号 */
	private String phone;
	/** 身份证 */
	private String idCard;
	/** 微信用户ID */
	private String openid;
	/** 最近登录时间 */
	private String lastLogin;
	/** 是否禁用 */
	private String isBanned;
	@TableField(exist = false)
	private String token;



}
