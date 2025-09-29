package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 门票
 */
@Data
@TableName("ticket")
public class Ticket  {

    /** id */
    @TableId(type = IdType.AUTO)
    private Integer id;
	/** 门票名称 */
	private String name;
	/** 门票价格 */
	private Double price;
	/** 门票描述 */
	private String content;



}
