package com.example.entity;

import cn.hutool.core.annotation.Alias;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 动物信息
 */
@Data
@TableName("animal")
public class Animal  {

    /** id */
    @TableId(type = IdType.AUTO)
    private Integer id;
	/** 动物名称 */
	@Alias("动物名称")
	private String name;
	/** 所属类别 */
	@TableField(exist = false)
	private String type;
	/** 动物介绍 */
	@Alias("动物介绍")
	private String content;
	/** 动物分类Id */
	private Integer typeId;
	/** 动物图片 */
	@Alias("动物图片")
	private String picUrl;

	@Alias("经度")
	private Double x;

	@Alias("纬度")
	private Double y;

	@TableField(exist = false)
	private Double distance;

}
