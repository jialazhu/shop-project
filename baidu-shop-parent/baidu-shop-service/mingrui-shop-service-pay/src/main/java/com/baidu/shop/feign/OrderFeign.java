package com.baidu.shop.feign;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.OrderInfo;
import com.baidu.shop.entity.OrderStatusEntity;
import org.json.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @ClassName OrderFeign
 * @Description: TODO
 * @Author jlz
 * @Date 2020-10-22 19:31
 * @Version V1.0
 **/
@FeignClient(value = "order-service",contextId = "OrderFeign")
public interface OrderFeign {

    @GetMapping(value = "order/getOrderById")
    Result<OrderInfo> getOrderById(@RequestParam String orderId);

    @PutMapping(value = "order/updateOrderStatus")
    Result<JSONObject> updateOrderStatus(@SpringQueryMap OrderStatusEntity orderStatusEntity);
}
