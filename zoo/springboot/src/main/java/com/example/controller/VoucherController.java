package com.example.controller;

import com.example.common.Result;
import com.example.entity.Voucher;
import com.example.service.VoucherService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *  描述：优惠券相关接口
 */
@RestController
@RequestMapping("/voucher")
public class VoucherController {

    @Resource
    VoucherService voucherService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Voucher voucher) {
        voucherService.add(voucher);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        voucherService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result delete(@RequestBody List<Integer> ids) {
        voucherService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 更新
     */
    @PutMapping("/update")
    public Result update(@RequestBody Voucher voucher) {
        voucherService.updateById(voucher);
        return Result.success();
    }

    /**
     * 查询单个
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Voucher voucher = voucherService.selectById(id);
        return Result.success(voucher);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Voucher voucher) {
        List<Voucher> list = voucherService.selectAll(voucher);
        return Result.success(list);
    }

    /**
     * 查询可领取的优惠券
     */
    @GetMapping("/selectAvailable")
    public Result selectAvailable() {
        List<Voucher> list = voucherService.selectAvailable();
        return Result.success(list);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectPage")
    public Result selectPage(
            Voucher voucher,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Voucher> pageInfo = voucherService.selectPage(voucher, pageNum, pageSize);
        return Result.success(pageInfo);
    }


}
