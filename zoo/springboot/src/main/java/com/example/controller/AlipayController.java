package com.example.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.example.common.Result;
import com.example.entity.Order;
import com.example.service.AlipayService;
import com.example.service.OrderService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/alipay")
public class AlipayController {
    @Autowired
    private AlipayService alipayService;
    @Resource
    private OrderService orderService;


    @PostMapping("/createOrder")
    public Result createOrder(@RequestBody Order order) {
        return Result.success(order.getOrderNumber());
    }

    @RequestMapping("/payPage")
    public void payPage(@RequestParam String orderNumber, HttpServletResponse response) throws Exception {
        Order order = orderService.getByOrderNumber(orderNumber);
        String html = alipayService.generatePayHtml(order);
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(html);
        response.getWriter().flush();
        response.getWriter().close();
    }

    @PostMapping("/refund")
    public Result refund(@RequestBody Order order) throws AlipayApiException {
        Order refund = alipayService.refund(order);
        if (refund != null) {
            order.setState("已退款");
            orderService.updateById(order);
            return Result.success("退款成功");
        }else {
            return Result.success("退款失败");
        }
    }

    @PostMapping("/tradeQuery")
    public Result tradeQuery(@RequestBody Order order) throws AlipayApiException {
        boolean tradeQuery = alipayService.AlipayTradeQuery(order.getOrderNumber());
        if (tradeQuery) {
            order.setState("待使用");
            orderService.updateById(order);
            return Result.success("支付成功");
        }else {
            order.setState("待支付");
            orderService.updateById(order);
            return Result.success("支付失败");
        }
    }


    @PostMapping("/notify")
    public void handleNotify(
            @RequestParam Map<String, String> params,
            HttpServletResponse response) throws IOException, AlipayApiException {
        String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAy5H4TrHHautRgb71Cy5ruhuovjDeMV+/pV+SJZs2fyLTatOELkaan7lUyoG6idOq2tAsW5YZH4uKJPHk5qFmYprIEJ8XG2FxhHSUIvByMsASp0oq0zyRibgG3W+1ceJEH7uIvcsg1r6Y5P4y3WIvDw6CPC7nVTRd76507C8vsQmVcKego+jaBE4NRk4GqhZKzay7dWIVDL00xEHvL5yefxRCXbABZe7T/IYr+oLGWX1WyNdU89rt+RyAGzStKb42dz1OzEK0byIdZi5LwN03WFYy0LBFwD4j++JoVV9IXe9C1DA2oXnVcsWGl4Zz3z2I2bDFo362QvZtbzikJi9Y1QIDAQAB";

        // 1. 验证签名
        boolean signVerified = AlipaySignature.rsaCheckV1(
                params,
                alipayPublicKey,
                "UTF-8",
                "RSA2"
        );

        if (!signVerified) {
            response.getWriter().write("fail");
            return;
        }

        // 2. 获取订单状态
        String tradeStatus = params.get("trade_status");
        String outTradeNo = params.get("out_trade_no");
        // 3. 根据交易状态更新订单
        if ("TRADE_SUCCESS".equals(tradeStatus)) {
            Order order = orderService.getByOrderNumber(outTradeNo);
            order.setState("待使用");
            orderService.updateById(order);
        }
        // 4. 必须返回 "success"，否则支付宝会重复通知
        response.getWriter().write("success");
    }

}

