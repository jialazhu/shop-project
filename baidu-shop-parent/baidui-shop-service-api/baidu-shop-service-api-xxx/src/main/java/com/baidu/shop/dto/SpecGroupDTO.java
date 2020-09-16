package com.baidu.shop.dto;

import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @ClassName SpecGroupEntity
 * @Description: TODO
 * @Author jlz
 * @Date 2020-09-03 11:55
 * @Version V1.0
 **/
@ApiModel(value = "规格组DTO")
@Data
public class SpecGroupDTO {

    @ApiModelProperty(value = "规格组主键",example = "1")
    @NotNull(message = "规格组主键不能为空",groups = {MingruiOperation.Update.class})
    private Integer id;

    @ApiModelProperty(value = "商品分类id",example = "1")
    @NotNull(message = "商品分类id不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private Integer cid;

    @ApiModelProperty(value = "规格组名称")
    @NotEmpty(message = "规格组名称不存在",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private String name;
}
