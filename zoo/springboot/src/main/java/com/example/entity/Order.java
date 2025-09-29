package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 订单
 */
@Data
@TableName("order")
public class Order  {

    /** id */
    @TableId(type = IdType.AUTO)
    private Integer id;
	/** 订单号 */
	private String orderNumber;
	/** 所购门票 */
	@TableField(exist = false)
	private String ticket;
	/** 购买数量 */
	private Integer number;
	/** 订单总价 */
	private Double totalPrice;
	/** 下单手机号 */
	@TableField(exist = false)
	private String phone;
	/** 下单时间 */
	private String createTime;
	/** 使用日期 */
	private String useDate;
	/** 订单状态 */
	private String state;
	/** 用户Id */
	private Integer userId;
	/** 门票Id */
	private Integer ticketId;
	/** 优惠券Id */
	private Integer voucherId;
	/** 优惠金额*/
	private Double discountAmount;


}
