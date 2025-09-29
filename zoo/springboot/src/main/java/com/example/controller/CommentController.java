package com.example.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.example.common.Result;
import com.example.entity.Comment;
import com.example.service.CommentService;
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
*  描述：评论相关接口
*/
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    CommentService commentService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Comment comment) {
        commentService.add(comment);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        commentService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result delete(@RequestBody List<Integer> ids) {
        commentService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 更新
     */
    @PutMapping("/update")
    public Result update(@RequestBody Comment comment) {
        commentService.updateById(comment);
        return Result.success();
    }

    /**
     * 查询单个
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Comment comment = commentService.selectById(id);
        return Result.success(comment);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Comment comment) {
        List<Comment> list = commentService.selectAll(comment);
        return Result.success(list);
    }
	/**
 	* 评论按点赞数排序
 	*/
	@GetMapping("/selectByApprove")
	public Result getCommentsOrderByApprove(){
		List<Comment> list = commentService.getCommentsOrderByApprove();
		return Result.success(list);
	}

	/**
	 * 评论按最新日期排序
	 */
	@GetMapping("/selectByDate")
	public Result getCommentsOrderByDate(){
		List<Comment> list = commentService.getCommentsOrderByDate();
		return Result.success(list);
	}


    /**
     * 查询所有
     */
    @GetMapping("/selectPage")
    public Result selectPage(
            Comment comment,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Comment> pageInfo = commentService.selectPage(comment, pageNum, pageSize);
        return Result.success(pageInfo);
    }
	/**
	 * 评论评分
	 */
	@GetMapping("/getScore")
	public Result getScore(){
		float score = commentService.getScore();
		return Result.success(score);
	}


	/**
	 * 描述：批量导出到excel
	 */
	@GetMapping("/export")
	public void export(HttpServletResponse response) throws IOException {
		List<Comment> all = commentService.selectAll(new Comment());
		List<Map<String, Object>> list = new ArrayList<>(all.size());
		if (CollectionUtil.isEmpty(all)) {
			Map<String, Object> row = new LinkedHashMap<>();
			row.put("用户", null);
			row.put("评分", null);
			row.put("评论内容", null);
			row.put("点赞数", null);
			row.put("评论时间", null);
			list.add(row);
		} else {
			for (Comment comment : all) {
				Map<String, Object> row = new LinkedHashMap<>();
				row.put("用户", comment.getUser());
				row.put("评分", comment.getScore());
				row.put("评论内容", comment.getContent());
				row.put("点赞数", comment.getApprove());
				row.put("评论时间", comment.getTime());
				list.add(row);
			}
		}
		ExcelWriter writer = ExcelUtil.getWriter(true);
		writer.write(list, true);
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
		response.setHeader("Content-Disposition","attachment;filename=commentInfoExcel.xlsx");
		ServletOutputStream out = response.getOutputStream();
		writer.flush(out, true);
		writer.close();
		IoUtil.close(System.out);
	}


}
