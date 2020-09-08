package com.baidu.shop.dto;

import com.baidu.shop.base.BaseDTO;
import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @ClassName SpuEntity
 * @Description: TODO
 * @Author jlz
 * @Date 2020-09-07 14:10
 * @Version V1.0
 **/
@ApiModel(value = "商品spu的DTO")
@Data
public class SpuDTO extends BaseDTO {

    @ApiModelProperty(value = "spu主键",example = "1")
    @NotNull(message = "spu主键不能为空",groups = {MingruiOperation.Update.class})
    private Integer id;

    @ApiModelProperty(value = "spu标题")
    @NotEmpty(message = "spu标题不存在",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private String title;

    @ApiModelProperty(value = "spu子标题")
    private String subTitle;

    @ApiModelProperty(value = "1级类目id")
    @NotNull(message = "1级类目id不存在",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private Integer cid1;

    @ApiModelProperty(value = "2级类目id")
    @NotNull(message = "2级类目id不存在",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private Integer cid2;

    @ApiModelProperty(value = "3级类目id")
    @NotNull(message = "3级类目id不存在",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private Integer cid3;

    @ApiModelProperty(value = "品牌id")
    @NotNull(message = "品牌id不存在",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private Integer brandId;

    @ApiModelProperty(value = "是否上架,0下架,1上架" ,example = "1")
    private Integer saleable;

    @ApiModelProperty(value = "是否有效,0删除,1有效" ,example = "1")
    private Integer valid;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;

    @ApiModelProperty(value = "最后修改时间")
    private Date lastUpdateTime;

    private String brandName;

    private String cateNames;

    @ApiModelProperty(value = "spu详细数据对象")
    private SpuDetailDTO spuDetail;

    @ApiModelProperty(value = "sku集合")
    private List<SkuDTO> skus;
}
