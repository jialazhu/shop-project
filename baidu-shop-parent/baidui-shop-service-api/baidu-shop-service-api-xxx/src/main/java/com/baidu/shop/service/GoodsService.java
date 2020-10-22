package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.dto.StockDTO;
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

    @ApiOperation(value = "查询商品")
    @GetMapping("goods/list")
    Result<List<SpuDTO>> select(@SpringQueryMap SpuDTO spuDTO);

    @ApiOperation(value = "新增商品")
    @PostMapping("goods/save")
    Result<JsonObject> save(@RequestBody SpuDTO spuDTO);

    @ApiOperation(value = "修改商品")
    @PutMapping("goods/save")
    Result<JsonObject> edit(@RequestBody SpuDTO spuDTO);

    @ApiOperation(value = "通过spuId查询spuDetail")
    @GetMapping("goods/getSpuDetailBySpuId")
    Result<SpuDetailEntity> getSpuDetailBySpuId(@RequestParam Integer spuId);

    @GetMapping("goods/getSkuAndStockBySpuId")
    @ApiOperation(value = "通过spuId查询Sku和Stock")
    Result<List<SkuDTO>> getSkuAndStockBySpuId(@RequestParam Integer spuId);

    @ApiOperation(value = "通过skuId查询Sku")
    @GetMapping("goods/getSkuBySkuId")
    Result<SkuEntity> getSkuBySkuId(@RequestParam Long skuId);

    @ApiOperation(value = "通过spuId查询Sku和Stock")
    @DeleteMapping("goods/delete")
    Result<JsonObject> delete(Integer spuId);

    @ApiOperation(value = "修改上下架状态")
    @PutMapping("goods/editSaleable")
    Result<JsonObject> editSaleable(@RequestBody SpuDTO spuDTO);

    @ApiOperation(value = "修改商品库存")
    @PutMapping("goods/updateStock")
    Result<JsonObject> updateStock(@RequestBody StockDTO stockDTO);
}
