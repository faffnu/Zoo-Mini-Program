package com.example.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.example.common.Result;
import com.example.entity.Order;
import com.example.service.OrderService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
*  描述：订单相关接口
*/
@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    OrderService orderService;

	@GetMapping("/getTicketStats")
	//获取统计图数据
	public Result getTicketStats(){
		//包装折线图数据
		List<Order> list = orderService.selectAll(new Order());
		// 用于存储每个月的统计结果
		List<Map<String, Object>> stats = new ArrayList<>();
		// 格式化日期
		SimpleDateFormat inputSdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat monthSdf = new SimpleDateFormat("MM");
		// 用于统计每个月的购票数量和总金额
		Map<String, Map<String, Integer>> monthlyStats = new HashMap<>();
		for (Order order : list) {
			try {
				Date date = inputSdf.parse(order.getUseDate());
				// 格式化 Date 对象为月份字符串
				String month = monthSdf.format(date);
				// 初始化每个月的统计数据
				if (!monthlyStats.containsKey(month)) {
					monthlyStats.put(month, new HashMap<String, Integer>() {{
						put("count", 0);
						put("totalAmount", 0);
					}});
				}
				if (Objects.equals(order.getState(), "已完成")||Objects.equals(order.getState(), "待评价")) {
					// 更新统计数据
					monthlyStats.get(month).put("count", monthlyStats.get(month).get("count") + order.getNumber());
					monthlyStats.get(month).put("totalAmount", monthlyStats.get(month).get("totalAmount") + (Integer.valueOf(order.getTotalPrice().intValue())));
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		Map<String, Map<String, Integer>> sortedMonthlyStats = new TreeMap<>(monthlyStats);
		// 将统计数据转换为前端需要的格式
		for (Map.Entry<String, Map<String, Integer>> entry : sortedMonthlyStats.entrySet()) {
			Map<String, Object> monthData = new HashMap<>();
			monthData.put("month", entry.getKey());
			monthData.put("count", entry.getValue().get("count"));
			monthData.put("totalAmount", entry.getValue().get("totalAmount"));
			stats.add(monthData);
		}
		// 将统计数据放入返回结果中
		return Result.success(stats);
	}


	@GetMapping("/getPie")
	//获取统计图数据
	public Result getPie() {

		List<Order> list = orderService.selectAll(new Order());
		// 统计每个门票的数量
		Map<String, Long> TicketCountMap = list.stream()
				.collect(Collectors.groupingBy(Order::getTicket, Collectors.counting()));

		// 转换成饼图数据格式
		JSONArray pieChartData = new JSONArray();
		TicketCountMap.forEach((name, value) -> {
			JSONObject item = new JSONObject();
			item.put("name", name);
			item.put("value", value);
			pieChartData.add(item);
		});

		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("pieChartData", pieChartData);

		return Result.success(resultMap);
	}

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Order order) {

		Order order1 = orderService.add(order);
		return Result.success(order1);
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        orderService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result delete(@RequestBody List<Integer> ids) {
        orderService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 更新
     */
    @PutMapping("/update")
    public Result update(@RequestBody Order order) {
        orderService.updateById(order);
        return Result.success();
    }

	/**
	 * 取消
	 */
	@PutMapping("/cancel/{id}")
	public Result cancel(@PathVariable Integer id) {
        Order order = orderService.selectById(id);
		order.setState("已取消");
		orderService.updateById(order);
		return Result.success();
	}

	/**
	 * 核销
	 */
	@PutMapping("/check/{id}")
	public Result check(@PathVariable Integer id) {
		Order order = orderService.selectById(id);
		order.setState("待评价");
		orderService.updateById(order);
		return Result.success();
	}

	@PutMapping("/comment/{id}")
	public Result comment(@PathVariable Integer id) {
		Order order = orderService.selectById(id);
		order.setState("已完成");
		orderService.updateById(order);
		return Result.success();
	}

    /**
     * 查询单个
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Order order = orderService.selectById(id);
        return Result.success(order);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Order order) {
        List<Order> list = orderService.selectAll(order);
        return Result.success(list);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectPage")
    public Result selectPage(
            Order order,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Order> pageInfo = orderService.selectPage(order, pageNum, pageSize);
        return Result.success(pageInfo);
    }


	/**
	 * 描述：批量导出到excel
	 */
	@GetMapping("/export")
	public void export(HttpServletResponse response) throws IOException {
		List<Order> all = orderService.selectAll(new Order());
		List<Map<String, Object>> list = new ArrayList<>(all.size());
		if (CollectionUtil.isEmpty(all)) {
			Map<String, Object> row = new LinkedHashMap<>();
			row.put("订单号", null);
			row.put("所购门票", null);
			row.put("购买数量", null);
			row.put("订单总价", null);
			row.put("下单手机号", null);
			row.put("下单时间", null);
			row.put("使用日期", null);
			row.put("订单状态", null);
			list.add(row);
		} else {
			for (Order order : all) {
				Map<String, Object> row = new LinkedHashMap<>();
				row.put("订单号", order.getOrderNumber());
				row.put("所购门票", order.getTicket());
				row.put("购买数量", order.getNumber());
				row.put("订单总价", order.getTotalPrice());
				row.put("下单手机号", order.getPhone());
				row.put("下单时间", order.getCreateTime());
				row.put("使用日期", order.getUseDate());
				row.put("订单状态", order.getState());
				list.add(row);
			}
		}
		ExcelWriter writer = ExcelUtil.getWriter(true);
		writer.write(list, true);
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
		response.setHeader("Content-Disposition","attachment;filename=orderInfoExcel.xlsx");
		ServletOutputStream out = response.getOutputStream();
		writer.flush(out, true);
		writer.close();
		IoUtil.close(System.out);
	}


}
