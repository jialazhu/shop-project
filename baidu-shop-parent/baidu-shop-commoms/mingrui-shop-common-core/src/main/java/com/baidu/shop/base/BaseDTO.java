package com.baidu.shop.base;

import com.baidu.shop.utils.StringUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @ClassName BaseDTO
 * @Description: TODO
 * @Author jlz
 * @Date 2020-08-31 19:40
 * @Version V1.0
 **/
@ApiModel(value = "通用DTO")
@Data
public class BaseDTO {

    @ApiModelProperty(value = "页数")
    private Integer page;

    @ApiModelProperty(value = "每页条数")
    private Integer rows;

    @ApiModelProperty(value = "排序字段")
    private String sort;

    @ApiModelProperty(value = "是否倒序")
    private Boolean desc;

    @ApiModelProperty(hidden = true)
    public String getOrderByClause(){
        if(StringUtil.isNotEmpty(sort)) return sort+" "+(desc?"desc":"asc");
        return null;
    }
}
