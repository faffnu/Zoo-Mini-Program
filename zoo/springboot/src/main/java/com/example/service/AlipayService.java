package com.example.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.example.entity.Order;
import org.springframework.stereotype.Service;

@Service
public class AlipayService {

    public String generatePayHtml(Order order) throws AlipayApiException {
        // 初始化SDK
        AlipayClient alipayClient = new DefaultAlipayClient(getAlipayConfig());
        // 构造请求参数以调用接口
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();

        // 设置异步回调地址（支付宝服务器主动通知的地址）
        request.setNotifyUrl("https://18b2a7ab.r9.cpolar.top/alipay/notify");

        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        // 设置商户订单号
        model.setOutTradeNo(order.getOrderNumber());
        // 设置订单总金额
        model.setTotalAmount(order.getTotalPrice().toString());
        // 设置订单标题
        model.setSubject("购票");
        // 设置产品码
        model.setProductCode("FAST_INSTANT_TRADE_PAY");
        // 设置PC扫码支付的方式
        model.setQrPayMode("1");

        request.setBizModel(model);

        AlipayTradePagePayResponse response = alipayClient.pageExecute(request, "POST");

        return response.getBody();
    }

    public boolean AlipayTradeQuery(String orderNumber) throws AlipayApiException {
        // 初始化SDK
        AlipayClient alipayClient = new DefaultAlipayClient(getAlipayConfig());

        // 构造请求参数以调用接口
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();

        // 设置订单支付时传入的商户订单号
        model.setOutTradeNo(orderNumber);

        request.setBizModel(model);

        AlipayTradeQueryResponse response = alipayClient.execute(request);
        // 检查请求是否成功
        if (response.isSuccess()) {
            String tradeStatus = response.getTradeStatus();
            if ("WAIT_BUYER_PAY".equals(tradeStatus)) {
                return false;
            } else if ("TRADE_SUCCESS".equals(tradeStatus)) {
                return true;
            }
        } else {
            System.out.println("查询失败，错误码: " + response.getCode() + " 错误信息: " + response.getMsg()+
                    "\n"+response.getSubCode()+"\n"+response.getSubMsg());
        }
        return false;


    }

    public Order refund(Order order) throws AlipayApiException {
        // 初始化SDK
        AlipayClient alipayClient = new DefaultAlipayClient(getAlipayConfig());

        // 构造请求参数以调用接口
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        AlipayTradeRefundModel model = new AlipayTradeRefundModel();
        // 设置商户订单号
        model.setOutTradeNo(order.getOrderNumber());
        // 设置退款金额
        model.setRefundAmount(order.getTotalPrice().toString());
        // 设置退款原因说明
        model.setRefundReason("正常退款");

        request.setBizModel(model);

        AlipayTradeRefundResponse response = alipayClient.execute(request);
        System.out.println(response.getBody());

        if (response.isSuccess()) {
            order.setState("已退款");
            return order;
        } else {
            return null;
        }
    }
    private static com.alipay.api.AlipayConfig getAlipayConfig() {
        String privateKey  = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCa97rUcXKGli+05vD6egoI9mwdOUfDtrdGSItbjmuId6zRhUA/C4AWOysrKrZtlPaIEj9WGaHwbqOQgiSRx6cK1sspJtiVQomiSSuLx7RkbNB9UOk5pqakpT8+urUFCHI6R3Y73u+pfggIjHdwip1iXPjzw+zsDCDpKtmKqE6id0CzuRRVPs3AtiUDtAW3prGxok+GCasB7rgoMhkjk98B+nkIkMnxeTufekNoF6LKMentZOHEtNaEvLYkBsWyREt2UpOWYxk/Rp5LKr5VD6+XwEHmtpMBsrpJ7++zS0bUNdfqDy9PDfT97+TrlzuqT2AYnNhIPmrqCYRkh2ck/6vJAgMBAAECggEAWQWWvhOtxsLgBPvl0v3oZd2pneAZ6cQ85JMn+yCFhY+6J2J01cK1gg/bbKDMy4aXWPOijsOK06O7MniQqW4X95IMg0ddg+EFT8TYzyOZ7VbVLigc0pF5iXukKVT4SDvh4rjKovoeky9jTgXjVzNJ1geyu9YBdFhDT67d6VlmUs1ncFmK/bTAmfWIHSO6DmEg7+nPMfmp/NL/LYCvzRVgU1lsgzEa6idEsQKoywmS11/eMV+BjTfydOmYGiAJEbxps6w9oOzlFIAH9nt2cVVtzPaAQrQj4zj/Bn6OAUqnd7OnmIqX6V+CbtmwvOrc65E4X+Nyhac9xnPiJLRanc9wAQKBgQDnC1hfP/51/8e8lFAA6LybGND9zkk7SkSEvHQbz584Z4/MpqLGU8YDoIsfvc1BoJHqMTjonzM6HPrfU09Wyzhb1lcTV9O7cfrxYrz0KMyr/jN9ejAj5w42vv348aqWC6Z8/yzbCpZShe+eIdfbTrrQyDJ1cRZzDxO2F5sVjFuCYQKBgQCrtMYjWqpugEOVBhpsQplBrG/XyOGlowOM9Sdutbqt8SuuQU6HumhfwmSS3yZhzxZVIemyH8jEPxgG4N87Nja5cXxO3qNYIo7T0CTOw6B6EFPGwT+P6Sk7Mi7cGiE6uO0kp05cniyzq30R3h9go/FyJh8w1zLTGPaeJ1gIXGhyaQKBgCrzMfdq9wvKg7/rtj+lBEK0x00XRwfNk5U1T0RIQWoD3niF1aaWti3Ab3x0FS8JAXcOtWyRA73e20gevZX96zkvTDqdTg//bU3oNAYTe5DRjZLa3w4disfkk2Pu0O5ZthZwfU8u1aj7mwLDedQb+6CKRs8MOccj218zXjIQSIahAoGAaz8gOtjYduh6TcxgGJqIavKvcNIxzmlBQHunQ4kSrq5iIBb+W9xccONUOkBTEuhsBISIGQ+tMJZQBYvTIoK8L5vojsKUt+nt1/K76sFZrKQiD5SIhPSXIjg3+JYBh1IWHLy7PzI0rYxBNDXO12fO2QNim/jt79g5wZLFc4N0YXECgYAuJKuu8UePijuEppv4cW/NIGFJbdrjG504VkyiAC9ErFX2PeDljiNBfJCUsIycARDAvf+oPidMU5hIjKZCtQIA/di4IrfnUXvsfGGuHjBUpk/2BhW4rbgkSx2zOsGPf19oD6Pv93Zd/dAMwwJriCHNYSFWV3ADZnkdZeukTfHISQ==";
        String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAy5H4TrHHautRgb71Cy5ruhuovjDeMV+/pV+SJZs2fyLTatOELkaan7lUyoG6idOq2tAsW5YZH4uKJPHk5qFmYprIEJ8XG2FxhHSUIvByMsASp0oq0zyRibgG3W+1ceJEH7uIvcsg1r6Y5P4y3WIvDw6CPC7nVTRd76507C8vsQmVcKego+jaBE4NRk4GqhZKzay7dWIVDL00xEHvL5yefxRCXbABZe7T/IYr+oLGWX1WyNdU89rt+RyAGzStKb42dz1OzEK0byIdZi5LwN03WFYy0LBFwD4j++JoVV9IXe9C1DA2oXnVcsWGl4Zz3z2I2bDFo362QvZtbzikJi9Y1QIDAQAB";
        com.alipay.api.AlipayConfig alipayConfig = new com.alipay.api.AlipayConfig();
        alipayConfig.setServerUrl("https://openapi-sandbox.dl.alipaydev.com/gateway.do");
        alipayConfig.setAppId("2021000147669825");
        alipayConfig.setPrivateKey(privateKey);
        alipayConfig.setFormat("json");
        alipayConfig.setAlipayPublicKey(alipayPublicKey);
        alipayConfig.setCharset("UTF-8");
        alipayConfig.setSignType("RSA2");
        return alipayConfig;
    }

}
