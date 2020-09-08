package com.baidu.shop.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @ClassName SkuEntity
 * @Description: TODO
 * @Author jlz
 * @Date 2020-09-08 19:14
 * @Version V1.0
 **/
@ApiModel(value = "sku的DTO")
@Data
public class SkuDTO {

    @ApiModelProperty(value = "skuID")
    private Long id;

    @ApiModelProperty(value = "spuID")
    private Integer spuId;

    @ApiModelProperty(value = "商品标题")
    private String title;

    @ApiModelProperty(value = "商品图片.多个图片用,分割")
    private String images;

    @ApiModelProperty(value = "销售价格,单位为分")
    private Long price;

    @ApiModelProperty(value = "特有规格属性在spu属性模版中的对应下标组合")
    private String indexes;

    @ApiModelProperty(value = "sku的特有规格参数键值对.json格式.反序列化时请使用linkedHashMap,保证有序")
    private String ownSpec;

    @ApiModelProperty(value = "是否有效,0无效,1有效")
    private Boolean enable;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;

    @ApiModelProperty(value = "最后修改时间")
    private Date lastUpdateTime;

    @ApiModelProperty(value = "库存数量")
    private Integer stock;
}
