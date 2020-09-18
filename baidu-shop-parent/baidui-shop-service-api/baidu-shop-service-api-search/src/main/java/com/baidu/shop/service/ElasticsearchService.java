package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.document.GoodsDoc;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

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
    Result<List<GoodsDoc>> search(String search);
}
