package com.example.controller;

import com.example.common.Result;
import com.example.entity.UserVoucher;
import com.example.service.UserVoucherService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *  描述：用户优惠券相关接口
 */
@RestController
@RequestMapping("/userVoucher")
public class UserVoucherController {

    @Resource
    UserVoucherService userVoucherService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody UserVoucher userVoucher) {

        userVoucherService.add(userVoucher);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        userVoucherService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result delete(@RequestBody List<Integer> ids) {
        userVoucherService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 更新
     */
    @PutMapping("/update")
    public Result update(@RequestBody UserVoucher userVoucher) {

        userVoucherService.updateById(userVoucher);
        return Result.success();
    }

    /**
     * 查询单个
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Long id) {
        UserVoucher userVoucher = userVoucherService.selectById(id);
        return Result.success(userVoucher);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(UserVoucher userVoucher) {
        List<UserVoucher> list = userVoucherService.selectAll(userVoucher);
        return Result.success(list);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectPage")
    public Result selectPage(
            UserVoucher userVoucher,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<UserVoucher> pageInfo = userVoucherService.selectPage(userVoucher, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    /**
     * 获取用户可用优惠券列表
     */
    @GetMapping("/usable/{userId}")
    public Result getUsableVouchers(@PathVariable Integer userId) {
        List<UserVoucher> usableVouchers = userVoucherService.getUsableVouchers(userId);
        return Result.success(usableVouchers);
    }

    /**
     * 获取用户所有优惠券（包含各种状态）
     */
    @GetMapping("/all/{userId}")
    public Result getAllUserVouchers(@PathVariable Integer userId) {
        List<UserVoucher> allVouchers = userVoucherService.getAllUserVouchers(userId);
        return Result.success(allVouchers);
    }

    /**
     * 用户领取优惠券
     */
    @PostMapping("/receive/{id}")
    public Result receiveVoucher(@PathVariable("id") Integer voucherId) {
        return userVoucherService.receiveVoucher(voucherId);
    }

    /**
     * 使用优惠券
     */
    @PostMapping("/use/{userVoucherId}")
    public Result useVoucher(@PathVariable Long userVoucherId,
                             @RequestParam Integer orderId) {
        boolean result = userVoucherService.useVoucher(userVoucherId, orderId);
        return result ? Result.success("使用成功") : Result.error("使用失败");
    }

    /**
     * 检查并更新过期优惠券
     */
    @PostMapping("/check-expired")
    public Result checkExpiredVouchers() {
        userVoucherService.checkAndUpdateExpiredVouchers();
        return Result.success();
    }

}
