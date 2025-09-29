package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Voucher;

import java.util.List;

public interface VoucherMapper extends BaseMapper<Voucher> {
    /**
     * 查询所有
     */
    List<Voucher> selectAll(Voucher voucher);

    /**
     * 根据ID查询
     */
    Voucher selectById(Integer id);

    /**
     * 删除
     */
    int deleteById(Integer id);

    List<Voucher> selectAvailable();

    void updateStock(Integer id);
}
