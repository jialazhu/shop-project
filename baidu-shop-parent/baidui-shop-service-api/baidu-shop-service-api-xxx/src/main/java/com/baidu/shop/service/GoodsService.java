package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.SpuEntity;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Api(tags = "商品接口")
public interface GoodsService {

    @GetMapping("goods/list")
    @ApiOperation(value = "查询商品")
    Result<PageInfo<SpuEntity>> select(SpuDTO spuDTO);

    @PostMapping("goods/save")
    @ApiOperation(value = "新增商品")
    Result<JsonObject> save(@RequestBody SpuDTO spuDTO);
}
