package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

@Api(tags = "es接口")
public interface ElasticsearchService {

    @ApiOperation(value = "获取商品测试")
    @GetMapping("es/goodsInfo")
    Result<JsonObject> esGoodsInfo();
}
