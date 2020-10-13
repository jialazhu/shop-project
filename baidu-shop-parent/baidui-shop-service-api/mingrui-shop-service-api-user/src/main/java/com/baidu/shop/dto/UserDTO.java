package com.baidu.shop.dto;

import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @ClassName UserEntity
 * @Description: TODO
 * @Author jlz
 * @Date 2020-10-13 14:29
 * @Version V1.0
 **/
@Data
@ApiModel(value = "用户dto")
public class UserDTO {

    @ApiModelProperty(value = "用户主键",example = "1")
    @NotNull(message = "主键不能为空",groups = {MingruiOperation.Update.class})
    private Integer id;

    @ApiModelProperty(value = "用户名")
    @NotNull(message = "用户名不能为空",groups = {MingruiOperation.Add.class})
    private String username;

    @ApiModelProperty(value = "用户密码")
    @NotNull(message = "用户密码不能为空",groups = {MingruiOperation.Add.class})
    private String password;

    @ApiModelProperty(value = "用户手机")
    @NotNull(message = "用户手机不能为空",groups = {MingruiOperation.Add.class})
    private String phone;

    @ApiModelProperty(hidden = true)
    private Date created;

    @ApiModelProperty(hidden = true)
    private String salt;

}
