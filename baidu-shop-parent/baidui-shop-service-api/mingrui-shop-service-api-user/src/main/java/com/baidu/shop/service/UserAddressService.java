package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.UserAddressDTO;
import com.baidu.shop.entity.UserAddressEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.JSONObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName UserAddressService
 * @Description: TODO
 * @Author jlz
 * @Date 2020-10-21 14:56
 * @Version V1.0
 **/
@Api(tags = "用户地址接口")
public interface UserAddressService {

    @ApiOperation(value = "通过用户Id查询所有地址")
    @GetMapping(value = "user/selectAddressByuserId")
    Result<List<UserAddressEntity>> selectAddressByuserId(@CookieValue(value = "SHOP_TOKEN") String token);

    @ApiOperation(value = "通过Id查询地址")
    @GetMapping(value = "user/selectAddressById")
    Result<UserAddressEntity> selectAddressById(Integer id);


    @ApiOperation(value = "新增地址")
    @PostMapping(value = "user/saveAddr")
    Result<JSONObject> saveAddr(@Validated({MingruiOperation.Add.class}) @RequestBody UserAddressDTO userAddressDTO,@CookieValue(value = "SHOP_TOKEN") String token);

    @ApiOperation(value = "修改地址")
    @PutMapping(value = "user/editAddr")
    Result<JSONObject> editAddr(@Validated({MingruiOperation.Update.class}) @RequestBody UserAddressDTO userAddressDTO);

    @ApiOperation(value = "删除地址")
    @DeleteMapping(value = "user/delAddr")
    Result<JSONObject> delAddr(Integer id);
}
