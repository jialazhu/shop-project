package com.baidu.shop.dto;

import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName Car
 * @Description: TODO
 * @Author jlz
 * @Date 2020-10-19 19:34
 * @Version V1.0
 **/
@Data
@ApiModel(value = "购物车数据")
public class Car {

    @ApiModelProperty(value = "用户ID",example = "1")
    private Integer userId;

    @ApiModelProperty(value = "skuID")
    @NotNull(message = "skuId不能为空",groups = {MingruiOperation.Add.class})
    private Long skuId;

    @ApiModelProperty(value = "sku标题")
    private String title;

    @ApiModelProperty(value = "sku图片")
    private String image;

    @ApiModelProperty(value = "skuID")
    @NotNull(message = "skuId不能为空",groups = {MingruiOperation.Add.class})
    private Integer num;

    @ApiModelProperty(value = "skuID")
    private String ownSpec;

    @ApiModelProperty(value = "skuID")
    private Long price;

}
