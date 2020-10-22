package com.baidu.shop.business;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.OrderDTO;
import com.baidu.shop.dto.OrderInfo;
import com.baidu.shop.entity.OrderStatusEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

@Api(tags = "订单接口")
public interface OrderService {

    @ApiOperation(value = "创建订单")
    @PostMapping(value = "order/createOrder")
    Result<Long> createOrder(@RequestBody OrderDTO orderDTO, @CookieValue("SHOP_TOKEN") String token);

    @ApiOperation(value = "通过订单id查询订单")
    @GetMapping(value = "order/getOrderById")
    Result<OrderInfo> getOrderById(String orderId);

    @ApiOperation(value = "修改订单状态")
    @PutMapping(value = "order/updateOrderStatus")
    Result<JSONObject> updateOrderStatus(OrderStatusEntity orderStatusEntity);
}