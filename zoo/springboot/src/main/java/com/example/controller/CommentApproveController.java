package com.example.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.example.common.Result;
import com.example.entity.Comment;
import com.example.entity.CommentApprove;
import com.example.service.CommentApproveService;
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
*  描述：评论点赞相关接口
*/
@RestController
@RequestMapping("/commentApprove")
public class CommentApproveController {

    @Resource
    CommentApproveService commentApproveService;
    @Resource
    CommentService commentService;


    /**
     * 切换点赞状态
     */
    @PostMapping("/approveToggle")
    public Result approveToggle(@RequestBody CommentApprove commentApprove) {
        commentApproveService.approveToggle(commentApprove);
        return Result.success();
    }

    /**
     * 新增点赞
     */
    @PostMapping("/add")
    public Result add(@RequestBody CommentApprove commentApprove) {
        Integer commentId = commentApprove.getCommentId();
        Comment comment = commentService.selectById(commentId);
        comment.setApprove(comment.getApprove() + 1);
        commentService.updateById(comment);
        commentApproveService.add(commentApprove);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        commentApproveService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result delete(@RequestBody List<Integer> ids) {
        commentApproveService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 更新
     */
    @PutMapping("/update")
    public Result update(@RequestBody CommentApprove commentApprove) {

        commentApproveService.updateById(commentApprove);
        return Result.success();
    }

    /**
     * 查询单个
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        CommentApprove commentApprove = commentApproveService.selectById(id);
        return Result.success(commentApprove);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(CommentApprove commentApprove) {
        List<CommentApprove> list = commentApproveService.selectAll(commentApprove);
        return Result.success(list);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectPage")
    public Result selectPage(
            CommentApprove commentApprove,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<CommentApprove> pageInfo = commentApproveService.selectPage(commentApprove, pageNum, pageSize);
        return Result.success(pageInfo);
    }


	/**
	 * 描述：批量导出到excel
	 */
	@GetMapping("/export")
	public void export(HttpServletResponse response) throws IOException {
		List<CommentApprove> all = commentApproveService.selectAll(new CommentApprove());
		List<Map<String, Object>> list = new ArrayList<>(all.size());
		if (CollectionUtil.isEmpty(all)) {
			Map<String, Object> row = new LinkedHashMap<>();
			row.put("用户", null);
			row.put("评论", null);
			list.add(row);
		} else {
			for (CommentApprove commentApprove : all) {
				Map<String, Object> row = new LinkedHashMap<>();
				row.put("用户", commentApprove.getUser());
				row.put("评论", commentApprove.getComment());
				list.add(row);
			}
		}
		ExcelWriter writer = ExcelUtil.getWriter(true);
		writer.write(list, true);
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
		response.setHeader("Content-Disposition","attachment;filename=commentApproveInfoExcel.xlsx");
		ServletOutputStream out = response.getOutputStream();
		writer.flush(out, true);
		writer.close();
		IoUtil.close(System.out);
	}


}
