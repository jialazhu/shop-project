package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.response.EsResponse;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Api(tags = "es接口")
public interface ElasticsearchService {

    @ApiOperation(value = "删除索引数据")
    @GetMapping("es/cleanEsData")
    Result<JsonObject> cleanEsData();

    @ApiOperation(value = "创建索引数据")
    @GetMapping("es/initEsData")
    Result<JsonObject> initEsData();

    @ApiOperation(value = "根据字段查询索引")
    @GetMapping("es/search")
    EsResponse search(String search, Integer page,String filter);

    @ApiOperation(value = "根据spuId添加索引数据")
    @PostMapping("es/initEsData")
    Result<JsonObject> saveData(Integer spuId);

    @ApiOperation(value = "根据spuId删除索引数据")
    @DeleteMapping("es/initEsData")
    Result<JsonObject> deleteData(Integer spuId);

}
