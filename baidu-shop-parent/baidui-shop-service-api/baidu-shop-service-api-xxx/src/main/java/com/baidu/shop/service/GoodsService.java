package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.SkuEntity;
import com.baidu.shop.entity.SpuDetailEntity;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品接口")
public interface GoodsService {

    @GetMapping("goods/list")
    @ApiOperation(value = "查询商品")
    Result<List<SpuDTO>> select(@SpringQueryMap SpuDTO spuDTO);

    @PostMapping("goods/save")
    @ApiOperation(value = "新增商品")
    Result<JsonObject> save(@RequestBody SpuDTO spuDTO);

    @PutMapping("goods/save")
    @ApiOperation(value = "修改商品")
    Result<JsonObject> edit(@RequestBody SpuDTO spuDTO);

    @GetMapping("goods/getSpuDetailBySpuId")
    @ApiOperation(value = "通过spuId查询spuDetail")
    Result<SpuDetailEntity> getSpuDetailBySpuId(@RequestParam Integer spuId);

    @GetMapping("goods/getSkuAndStockBySpuId")
    @ApiOperation(value = "通过spuId查询Sku和Stock")
    Result<List<SkuDTO>> getSkuAndStockBySpuId(@RequestParam Integer spuId);

    @GetMapping("goods/getSkuBySkuId")
    @ApiOperation(value = "通过skuId查询Sku")
    Result<SkuEntity> getSkuBySkuId(@RequestParam Long skuId);

    @DeleteMapping("goods/delete")
    @ApiOperation(value = "通过spuId查询Sku和Stock")
    Result<JsonObject> delete(Integer spuId);

    @PutMapping("goods/editSaleable")
    @ApiOperation(value = "修改上下架状态")
    Result<JsonObject> editSaleable(@RequestBody SpuDTO spuDTO);
}
