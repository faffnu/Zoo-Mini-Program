package com.example.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.example.common.Result;
import com.example.entity.Favor;
import com.example.service.FavorService;
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
*  描述：用户收藏相关接口
*/
@RestController
@RequestMapping("/favor")
public class FavorController {

    @Resource
    FavorService favorService;

    /**
     * 切换收藏状态
     */
    @PostMapping("/favorToggle")
    public Result favorToggle(@RequestBody Favor favor) {

        favorService.favorToggle(favor);
        return Result.success();
    }

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Favor favor) {

        favorService.add(favor);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        favorService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result delete(@RequestBody List<Integer> ids) {
        favorService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 更新
     */
    @PutMapping("/update")
    public Result update(@RequestBody Favor favor) {

        favorService.updateById(favor);
        return Result.success();
    }

    /**
     * 查询用户收藏
     */
    @GetMapping("/selectByUserId/{userId}")
    public Result selectByUserId(@PathVariable int userId) {
        List<Favor> list = favorService.selectByUserId(userId);
        return Result.success(list);
    }

    /**
     * 查询单个
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Favor favor = favorService.selectById(id);
        return Result.success(favor);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Favor favor) {
        List<Favor> list = favorService.selectAll(favor);
        return Result.success(list);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectPage")
    public Result selectPage(
            Favor favor,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Favor> pageInfo = favorService.selectPage(favor, pageNum, pageSize);
        return Result.success(pageInfo);
    }


	/**
	 * 描述：批量导出到excel
	 */
	@GetMapping("/export")
	public void export(HttpServletResponse response) throws IOException {
		List<Favor> all = favorService.selectAll(new Favor());
		List<Map<String, Object>> list = new ArrayList<>(all.size());
		if (CollectionUtil.isEmpty(all)) {
			Map<String, Object> row = new LinkedHashMap<>();
			row.put("用户", null);
			row.put("动物", null);
			list.add(row);
		} else {
			for (Favor favor : all) {
				Map<String, Object> row = new LinkedHashMap<>();
				row.put("用户", favor.getUser());
				row.put("动物", favor.getAnimal());
				list.add(row);
			}
		}
		ExcelWriter writer = ExcelUtil.getWriter(true);
		writer.write(list, true);
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
		response.setHeader("Content-Disposition","attachment;filename=favorInfoExcel.xlsx");
		ServletOutputStream out = response.getOutputStream();
		writer.flush(out, true);
		writer.close();
		IoUtil.close(System.out);
	}


}
