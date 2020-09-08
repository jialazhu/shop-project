package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品规格接口")
public interface SpecificationService {

    @GetMapping("specification/listGroup")
    @ApiOperation(value = "查询品牌规格组")
    Result<List<SpecGroupEntity>> selectGroup(SpecGroupDTO specGroupDTO);

    @PostMapping("specification/saveGroup")
    @ApiOperation(value = "新增品牌规格组")
    Result<JsonObject> saveGroup(@Validated({MingruiOperation.Add.class}) @RequestBody SpecGroupDTO specGroupDTO);

    @PutMapping("specification/saveGroup")
    @ApiOperation(value = "修改品牌规格组")
    Result<JsonObject> editGroup(@Validated({MingruiOperation.Update.class}) @RequestBody SpecGroupDTO specGroupDTO);

    @DeleteMapping("specification/deleteGroup")
    @ApiOperation(value = "删除品牌规格组")
    Result<JsonObject> deleteGroup(Integer id);

    @GetMapping("specification/listParam")
    @ApiOperation(value = "查询品牌规格参数")
    Result<List<SpecParamEntity>> selectParam(SpecParamDTO specParamDTO);

    @PostMapping("specification/saveParam")
    @ApiOperation(value = "新增品牌规格参数")
    Result<JsonObject> saveParam(@Validated({MingruiOperation.Add.class}) @RequestBody SpecParamDTO specParamDTO);

    @PutMapping("specification/saveParam")
    @ApiOperation(value = "新增品牌规格参数")
    Result<JsonObject> editParam(@Validated({MingruiOperation.Update.class}) @RequestBody SpecParamDTO specParamDTO);

    @DeleteMapping("specification/deleteParam")
    @ApiOperation(value = "删除品牌规格参数")
    Result<JsonObject> deleteParam(Integer id);
}
