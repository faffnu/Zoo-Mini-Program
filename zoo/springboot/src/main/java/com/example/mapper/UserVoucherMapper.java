package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.UserVoucher;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserVoucherMapper extends BaseMapper<UserVoucher> {
    /**
     * 查询所有
     */
    List<UserVoucher> selectAll(UserVoucher userVoucher);

    /**
     * 根据ID查询
     */
    UserVoucher selectById(Long id);
    
    /**
     * 删除
     */
    int deleteById(Long id);

    List<UserVoucher> selectUsableVouchers(Integer userId);

    List<UserVoucher> selectAllUserVouchers(Integer userId);

    List<UserVoucher> selectExpiredVouchers(LocalDateTime now);

    int batchUpdateExpiredState(@Param("expiredIds")List<Long> expiredIds, @Param("updateTime")LocalDateTime updateTime);
}
