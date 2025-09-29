package com.example.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Voucher {
    private Integer id;
    private String title;
    private Integer type; // 1-折扣券，2-满减券
    private Double value;
    private Double minAmount;
    private Integer stock;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
