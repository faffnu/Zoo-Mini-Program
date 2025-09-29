package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserVoucher {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    private Integer userId;
    private Integer voucherId;
    private Integer state; // 0-未使用，1-已使用，2-已过期
    private LocalDateTime getTime;
    private LocalDateTime useTime;
    private Integer orderId;
    @TableField(exist = false)
    private String title;
    @TableField(exist = false)
    private Integer type; // 1-折扣券，2-满减券
    @TableField(exist = false)
    private Double value;
    @TableField(exist = false)
    private Double minAmount;
    @TableField(exist = false)
    private LocalDateTime beginTime;
    @TableField(exist = false)
    private LocalDateTime endTime;
}
