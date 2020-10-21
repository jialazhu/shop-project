package com.baidu.shop.business;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.OrderDTO;
import com.baidu.shop.entity.OrderEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Api(tags = "订单接口")
public interface OrderService {

    @ApiOperation(value = "创建订单")
    @PostMapping(value = "order/createOrder")
    Result<Long> createOrder(@RequestBody OrderDTO orderDTO, @CookieValue("SHOP_TOKEN") String token);

    @ApiOperation(value = "通过订单id查询订单")
    @GetMapping(value = "order/getOrderById")
    Result<OrderEntity> getOrderById(String orderId);
}