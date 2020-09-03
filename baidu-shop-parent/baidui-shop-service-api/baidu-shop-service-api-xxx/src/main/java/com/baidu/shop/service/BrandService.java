package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "商品品牌接口")
public interface BrandService {

    @GetMapping("brand/list")
    @ApiOperation(value = "查询品牌")
    Result<List<BrandEntity>> select(BrandDTO brandDTO);

    @PostMapping("brand/save")
    @ApiOperation(value = "新增品牌")
    Result<JsonObject> save( @Validated({MingruiOperation.Add.class}) @RequestBody BrandDTO brandDTO);

    @PutMapping("brand/save")
    @ApiOperation(value = "修改品牌")
    Result<JsonObject> edit( @Validated({MingruiOperation.Update.class}) @RequestBody BrandDTO brandDTO);

    @DeleteMapping("brand/delete")
    @ApiOperation(value = "删除品牌")
    Result<List<BrandEntity>> delete(Integer id);
}
