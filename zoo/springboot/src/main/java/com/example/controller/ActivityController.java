package com.example.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.example.common.Result;
import com.example.entity.Activity;
import com.example.service.ActivityService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
*  描述：活动相关接口
*/
@RestController
@RequestMapping("/activity")
public class ActivityController {

    @Resource
    ActivityService activityService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Activity activity) {
		List<String> timeSlot = activity.getTimeSlot();
		if (CollectionUtil.isNotEmpty(timeSlot)) {
			activity.setTime(timeSlot.get(0) + " ~ " + timeSlot.get(1));
		}

        activityService.add(activity);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        activityService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result delete(@RequestBody List<Integer> ids) {
        activityService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 更新
     */
    @PutMapping("/update")
    public Result update(@RequestBody Activity activity) {
		List<String> timeSlot = activity.getTimeSlot();
		if (CollectionUtil.isNotEmpty(timeSlot)) {
			activity.setTime(timeSlot.get(0) + " ~ " + timeSlot.get(1));
		}

        activityService.updateById(activity);
        return Result.success();
    }

    /**
     * 查询单个
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Activity activity = activityService.selectById(id);
        return Result.success(activity);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Activity activity) {
        List<Activity> list = activityService.selectAll(activity);
        return Result.success(list);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectPage")
    public Result selectPage(
            Activity activity,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Activity> pageInfo = activityService.selectPage(activity, pageNum, pageSize);
        return Result.success(pageInfo);
    }


	/**
	 * 描述：批量导出到excel
	 */
	@GetMapping("/export")
	public void export(HttpServletResponse response) throws IOException {
		List<Activity> all = activityService.selectAll(new Activity());
		List<Map<String, Object>> list = new ArrayList<>(all.size());
		if (CollectionUtil.isEmpty(all)) {
			Map<String, Object> row = new LinkedHashMap<>();
			row.put("活动标题", null);
			row.put("活动介绍", null);
			row.put("活动时间", null);
			row.put("发布日期", null);
			list.add(row);
		} else {
			for (Activity activity : all) {
				Map<String, Object> row = new LinkedHashMap<>();
				row.put("活动标题", activity.getTitle());
				row.put("活动介绍", activity.getIntroduce());
				row.put("活动时间", activity.getTime());
				row.put("发布日期", activity.getPublishDate());
				list.add(row);
			}
		}
		ExcelWriter writer = ExcelUtil.getWriter(true);
		writer.write(list, true);
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
		response.setHeader("Content-Disposition","attachment;filename=activityInfoExcel.xlsx");
		ServletOutputStream out = response.getOutputStream();
		writer.flush(out, true);
		writer.close();
		IoUtil.close(System.out);
	}


}
