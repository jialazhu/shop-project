package com.baidu.shop.entity;

import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @ClassName UserEntity
 * @Description: TODO
 * @Author jlz
 * @Date 2020-10-15 17:56
 * @Version V1.0
 **/
@Data
@Table(name = "tb_user")
@ApiModel(value = "用户dto")
public class UserEntity {

    @Id
    @ApiModelProperty(hidden = true)
    private Integer id;

    @ApiModelProperty(value = "用户名")
    @NotNull(message = "用户名不能为空",groups = {MingruiOperation.Add.class})
    private String username;

    @ApiModelProperty(value = "用户密码")
    @NotNull(message = "用户密码不能为空",groups = {MingruiOperation.Add.class})
    private String password;

    @ApiModelProperty(hidden = true)
    private String phone;

    @ApiModelProperty(hidden = true)
    private Date created;

    @ApiModelProperty(hidden = true)
    private String salt;

}