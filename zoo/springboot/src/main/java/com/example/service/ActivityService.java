package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.entity.Activity;
import com.example.mapper.ActivityMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActivityService {

    @Resource
    private ActivityMapper activityMapper;
    /**
     * 新增
     */
    public void add(Activity activity) {
        activityMapper.insert(activity);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        activityMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
            activityMapper.deleteBatchIds(ids);
    }

    /**
     * 修改
     */
    public void updateById(Activity activity) {
        activityMapper.updateById(activity);
    }

    /**
     * 根据ID查询
     */
    public Activity selectById(Integer id) {
        return activityMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Activity> selectAll(Activity activity) {
        return activityMapper.selectAll(activity);
    }

    /**
     * 分页查询
     */
    public PageInfo<Activity> selectPage(Activity activity, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<Activity> list = this.selectAll(activity);
		// 处理下时间段
		for (Activity dbActivity : list) {
			List<String> timeSlot = new ArrayList<>();
			String time = dbActivity.getTime();
			if (ObjectUtil.isNotEmpty(time)) {
				String[] split = time.split(" ~ ");
				timeSlot.add(split[0]);
				timeSlot.add(split[1]);
				}
			dbActivity.setTimeSlot(timeSlot);
		}

        return PageInfo.of(list);
    }

}