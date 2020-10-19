package com.baidu.shop;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.Car;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * @ClassName CarService
 * @Description: TODO
 * @Author jlz
 * @Date 2020-10-19 19:38
 * @Version V1.0
 **/
@Api(tags = "购物车接口")
public interface CarService {

    @ApiOperation(value = "添加商品到购物车")
    @PostMapping("car/addCar")
    Result<JSONObject> addCar(@RequestBody Car car , @CookieValue(value = "SHOP_TOKEN") String token);
}
