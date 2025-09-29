package com.example.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.example.common.Result;
import com.example.entity.Animal;
import com.example.service.AnimalService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
*  描述：动物信息相关接口
*/
@RestController
@RequestMapping("/animal")
public class AnimalController {

    @Resource
    AnimalService animalService;


    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Animal animal) {
		if (animalService.add(animal)){
			return Result.success("添加成功");
		}
		return Result.error("添加失败");
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
		if (animalService.deleteById(id)){
			return Result.success("删除成功");
		}
		return Result.error("删除失败");
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result delete(@RequestBody List<Integer> ids) {
		if (animalService.deleteBatch(ids)){
			return Result.success("批量删除成功");
		}
		return Result.error("批量删除失败");
    }

    /**
     * 更新
     */
    @PutMapping("/update")
    public Result update(@RequestBody Animal animal) {

        if (animalService.update(animal)){
			return Result.success("更新成功");
		}
		return Result.error("更新失败");

    }

    /**
     * 查询单个
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Animal animal = animalService.selectById(id);
        return Result.success(animal);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Animal animal) {
        List<Animal> list = animalService.selectAll(animal);
        return Result.success(list);
    }

    /**
     * 查询所有
     */
	@GetMapping("/selectPage")
	public Result selectPage(
			Animal animal,
			@RequestParam(value = "x", required = false) Double x,
			@RequestParam(value = "y", required = false) Double y,
			@RequestParam(value = "maxDistance", required = false) Integer maxDistance,
			@RequestParam(defaultValue = "1") Integer pageNum,
			@RequestParam(defaultValue = "10") Integer pageSize) {
		PageInfo<Animal> pageInfo = animalService.selectPage(animal, x, y, maxDistance, pageNum, pageSize);
		return Result.success(pageInfo);

	}


	/**
	 * 描述：批量导出到excel
	 */
	@GetMapping("/export")
	public void export(HttpServletResponse response) throws IOException {
		List<Animal> all = animalService.selectAll(new Animal());
		List<Map<String, Object>> list = new ArrayList<>(all.size());
		if (CollectionUtil.isEmpty(all)) {
			Map<String, Object> row = new LinkedHashMap<>();
			row.put("动物名称", null);
			row.put("所属类别", null);
			row.put("动物介绍", null);
			list.add(row);
		} else {
			for (Animal animal : all) {
				Map<String, Object> row = new LinkedHashMap<>();
				row.put("动物名称", animal.getName());
				row.put("所属类别", animal.getType());
				row.put("动物介绍", animal.getContent());
				list.add(row);
			}
		}
		ExcelWriter writer = ExcelUtil.getWriter(true);
		writer.write(list, true);
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
		response.setHeader("Content-Disposition","attachment;filename=animalInfoExcel.xlsx");
		ServletOutputStream out = response.getOutputStream();
		writer.flush(out, true);
		writer.close();
		IoUtil.close(System.out);
	}

	/**
	 * 描述：通过excel批量导入
	 */
	@PostMapping("/upload")
	public Result upload(MultipartFile file) throws IOException {
		List<Animal> infoList = ExcelUtil.getReader(file.getInputStream()).readAll(Animal.class);
		if (!CollectionUtil.isEmpty(infoList)) {
			for (Animal info : infoList) {
				try {
					animalService.add(info);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return Result.success();
	}

	/**
	 * 描述：下载excel模板
	 */
	@GetMapping("/download")
	public void download(HttpServletResponse response) throws IOException {
		List<Map<String, Object>> list = new ArrayList<>();
		Map<String, Object> row = new LinkedHashMap<>();
		row.put("动物名称", null);
		row.put("所属类别", null);
		row.put("动物介绍", null);
		list.add(row);
		ExcelWriter writer = ExcelUtil.getWriter(true);
		writer.write(list, true);
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
		response.setHeader("Content-Disposition","attachment;filename=animalInfoExcel.xlsx");
		ServletOutputStream out = response.getOutputStream();
		writer.flush(out, true);
		writer.close();
		IoUtil.close(System.out);
	}

}
