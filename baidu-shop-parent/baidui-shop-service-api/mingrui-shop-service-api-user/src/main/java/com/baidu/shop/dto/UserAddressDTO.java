package com.baidu.shop.dto;

import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


/**
 * @ClassName UserAddress
 * @Description: TODO
 * @Author jlz
 * @Date 2020-10-21 14:57
 * @Version V1.0
 **/
@Data
@ApiModel(value = "用户地址dto")
public class UserAddressDTO {

    @ApiModelProperty(value = "用户地址主键",example = "1")
    @NotNull(message = "主键不能为空",groups = {MingruiOperation.Update.class})
    private Integer id; //地址 id

    @ApiModelProperty(value = "用户Id")
    private Integer userId;//用户 id

    @ApiModelProperty(value = "收件人姓名")
    @NotEmpty(message = "收件人姓名不能为空",groups = {MingruiOperation.Add.class})
    private String name;// 收件人姓名

    @ApiModelProperty(value = "收件人电话")
    @NotEmpty(message = "收件人电话不能为空",groups = {MingruiOperation.Add.class})
    private String phone;//收件人电话

    @ApiModelProperty(value = "省份")
    @NotEmpty(message = "省份不能为空",groups = {MingruiOperation.Add.class})
    private String state;//省份

    @ApiModelProperty(value = "城市")
    @NotEmpty(message = "城市不能为空",groups = {MingruiOperation.Add.class})
    private String city;//城市

    @ApiModelProperty(value = "区")
    @NotEmpty(message = "区不能为空",groups = {MingruiOperation.Add.class})
    private String district;//区

    @ApiModelProperty(value = "街道地址")
    @NotEmpty(message = "街道地址不能为空",groups = {MingruiOperation.Add.class})
    private String address;//街道地址

    @ApiModelProperty(value = "邮编")
    private String zipCode;//邮编

    @ApiModelProperty(value = "是否是默认地址 默认false 1:true 0:false")
    private Boolean defaults;//是否是默认地址 默认false 1:true 0:false
}
