package com.baidu.shop.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @ClassName StockEntity
 * @Description: TODO
 * @Author jlz
 * @Date 2020-09-08 19:20
 * @Version V1.0
 **/
@ApiModel(value = "库存DTO")
@Data
public class StockDTO {

    @ApiModelProperty(value = "库存对应的skuID")
    private Long skuId;

    @ApiModelProperty(value = "可秒杀库存")
    private Integer seckillStock;

    @ApiModelProperty(value = "秒杀总数量")
    private Integer seckillTotal;

    @ApiModelProperty(value = "库存数量")
    private Integer stock;
}
