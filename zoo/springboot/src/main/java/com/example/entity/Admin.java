package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 管理员
 */
@Data
@TableName("admin")
public class Admin extends Account {

    /** id */
    @TableId(type = IdType.AUTO)
    private Integer id;
	/** 账号 */
	private String username;
	/** 密码 */
	private String password;
	/** 名称 */
	private String name;
	/** 头像 */
	private String avatar;



}
