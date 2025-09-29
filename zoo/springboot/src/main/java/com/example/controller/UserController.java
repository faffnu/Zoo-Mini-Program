package com.example.controller;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.example.common.Result;
import com.example.entity.User;
import com.example.service.UserService;
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
*  描述：用户相关接口
*/
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    UserService userService;




    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody User user) {

        userService.add(user);
        return Result.success();
    }

    /**
     * 禁用
     */
    @PutMapping ("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        userService.isBanById(id);
        return Result.success();
    }


    /**
     * 批量禁用
     */
    @DeleteMapping("/delete/batch")
    public Result ban(@RequestBody List<Integer> ids) {
        userService.banBatch(ids);
        return Result.success();
    }
	/**
	 * 批量解禁
	 */
	@DeleteMapping("/unban/batch")
	public Result unBan(@RequestBody List<Integer> ids) {
		userService.unBanBatch(ids);
		return Result.success();
	}

    /**
     * 更新
     */
    @PutMapping("/update")
    public Result update(@RequestBody User user) {

        userService.updateById(user);
        return Result.success();
    }

    /**
     * 查询单个
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        User user = userService.selectById(id);
        return Result.success(user);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(User user) {
        List<User> list = userService.selectAll(user);
        return Result.success(list);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectPage")
    public Result selectPage(
            User user,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<User> pageInfo = userService.selectPage(user, pageNum, pageSize);
        return Result.success(pageInfo);
    }



	/**
	 * 描述：批量导出到excel
	 */
	@GetMapping("/export")
	public void export(HttpServletResponse response) throws IOException {
		List<User> all = userService.selectAll(new User());
		List<Map<String, Object>> list = new ArrayList<>(all.size());
		if (CollectionUtil.isEmpty(all)) {
			Map<String, Object> row = new LinkedHashMap<>();
			row.put("昵称", null);
			row.put("性别", null);
			row.put("手机号", null);
			row.put("身份证", null);
			row.put("微信用户ID", null);
			row.put("最近登录时间", null);
			row.put("是否禁用", null);
			list.add(row);
		} else {
			for (User user : all) {
				Map<String, Object> row = new LinkedHashMap<>();
				row.put("昵称", user.getName());
				row.put("性别", user.getGender());
				row.put("手机号", user.getPhone());
				row.put("身份证", user.getIdCard());
				row.put("微信用户ID", user.getOpenid());
				row.put("最近登录时间", user.getLastLogin());
				row.put("是否禁用", user.getIsBanned());
				list.add(row);
			}
		}
		ExcelWriter writer = ExcelUtil.getWriter(true);
		writer.write(list, true);
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
		response.setHeader("Content-Disposition","attachment;filename=userInfoExcel.xlsx");
		ServletOutputStream out = response.getOutputStream();
		writer.flush(out, true);
		writer.close();
		IoUtil.close(System.out);
	}


}
