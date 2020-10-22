package com.baidu.shop.business;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Api(tags = "支付接口")
public interface PayService {

    @ApiOperation(value = "请求支付")
    @GetMapping(value = "pay/requestPay")
    void requestPay(String orderId, HttpServletResponse response , @CookieValue("SHOP_TOKEN")String token);

    @ApiOperation(value = "返回通知")
    @PostMapping(value = "pay/returnNoitfy")
    void returnNoitfy( HttpServletRequest request);

    @ApiOperation(value = "返回路径")
    @GetMapping(value = "pay/returnUrl")
    void returnUrl(HttpServletResponse response, HttpServletRequest request);
}