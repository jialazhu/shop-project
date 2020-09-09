package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Table(name = "tb_sku")
@Data
public class SkuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer spuId;

    private String title;

    private String images;

    private Long price;

    private String indexes;

    private String ownSpec;

    private Boolean enable;

    private Date createTime;

    private Date lastUpdateTime;
}