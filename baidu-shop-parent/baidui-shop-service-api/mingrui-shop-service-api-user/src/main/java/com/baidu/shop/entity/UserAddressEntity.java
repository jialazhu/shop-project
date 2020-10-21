package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @ClassName UserAddress
 * @Description: TODO
 * @Author jlz
 * @Date 2020-10-21 14:57
 * @Version V1.0
 **/
@Data
@Table(name = "tb_user_address")
public class UserAddressEntity {

    @Id
    private Integer id; //地址 id

    private Integer userId;//用户 id

    private String name;// 收件人姓名

    private String phone;//收件人电话

    private String state;//省份

    private String city;//城市

    private String district;//区

    private String address;//街道地址

    private String zipCode;//邮编

    private Boolean defaults;//默认选中0  1:true 0:false
}
