package com.baidu.shop;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.Car;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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

    @ApiOperation(value = "合并购物车")
    @PostMapping("car/mergeCar")
    Result<JSONObject> mergeCar(@RequestBody String skusList, @CookieValue(value = "SHOP_TOKEN") String token);

    @ApiOperation(value = "获得购物车")
    @GetMapping("car/getCar")
    Result<List<Car>> getCar(@CookieValue(value = "SHOP_TOKEN") String token);

    @ApiOperation(value = "从购物车中删除制定skuId的商品")
    @DeleteMapping("car/deleteCar")
    Result<List<Car>> deleteCar(String skuId,@CookieValue(value = "SHOP_TOKEN") String token);

    @ApiOperation(value = "修改购物车商品的数量")
    @GetMapping("car/updateNum")
    Result<List<Car>> updateNum(Long skuId,Integer type,@CookieValue(value = "SHOP_TOKEN") String token);
}
