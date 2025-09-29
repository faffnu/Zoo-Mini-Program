package com.example.entity;

import cn.hutool.core.annotation.Alias;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 动物分类
 */
@Data
@TableName("type")
public class Type  {

    /** id */
    @TableId(type = IdType.AUTO)
    private Integer id;
	/** 分类名称 */
	@Alias("分类名称")
	private String name;
	/** 分类描述 */
	@Alias("分类描述")
	private String content;



}
