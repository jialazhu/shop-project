package com.baidu.shop.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @ClassName SpuDetailEntity
 * @Description: TODO
 * @Author jlz
 * @Date 2020-09-08 19:09
 * @Version V1.0
 **/

@ApiModel(value = "spuDetail的DTO")
@Data
public class SpuDetailDTO {

    @ApiModelProperty(value = "spuID")
    private Integer spuId;

    @ApiModelProperty(value = "商品描述信息")
    private String description;

    @ApiModelProperty(value = "通用规格参数数据")
    private String genericSpec;

    @ApiModelProperty(value = "特有规格参数可选值信息.json格式")
    private String specialSpec;

    @ApiModelProperty(value = "包装清单")
    private String packingList;

    @ApiModelProperty(value = "售后服务")
    private String afterService;
}
