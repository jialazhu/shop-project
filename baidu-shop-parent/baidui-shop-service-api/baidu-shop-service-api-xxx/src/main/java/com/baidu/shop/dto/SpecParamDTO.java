package com.baidu.shop.dto;

import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @ClassName SpecParamDTO
 * @Description: TODO
 * @Author jlz
 * @Date 2020-09-03 17:53
 * @Version V1.0
 **/
@ApiModel(value = "规格参数DTO")
@Data
public class SpecParamDTO {

    @ApiModelProperty(value = "规格参数主键",example = "1")
    @NotNull(message = "规格参数主键不能为空",groups = {MingruiOperation.Update.class})
    private Integer id;

    @ApiModelProperty(value = "商品分类id",example = "1")
    @NotNull(message = "商品分类id不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private Integer cid;

    @ApiModelProperty(value = "规格组id",example = "1")
    @NotNull(message = "规格组id不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private Integer groupId;

    @ApiModelProperty(value = "规格参数名称")
    @NotEmpty(message = "规格参数名称不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private String  name ;

    @ApiModelProperty(value = "是否为数字类型",example = "1")
    @NotNull(message = "是否为数字类型不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private Boolean numeric;

    @ApiModelProperty(value = "数字类型参数单位")
    private String unit;

    @ApiModelProperty(value = "是否是sku通用属性",example = "1")
    @NotNull(message = "是否是sku通用属性不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private Boolean generic;

    @ApiModelProperty(value = "是否用于搜索",example = "1")
    @NotNull(message = "是否用于搜索不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private Boolean searching;

    @ApiModelProperty(value = "数值类型参数")
    private String segments;
}
