package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @ClassName BrandEntity
 * @Description: TODO
 * @Author jlz
 * @Date 2020-08-31 14:25
 * @Version V1.0
 **/
@Table(name = "tb_brand")
@Data
public class BrandEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //主键生成策略 并返回主键
    private Integer id;

    private String name;

    private String image;

    private Character letter;
}
