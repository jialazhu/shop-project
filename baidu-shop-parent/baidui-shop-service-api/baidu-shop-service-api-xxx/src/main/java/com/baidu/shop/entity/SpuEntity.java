package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @ClassName SpuEntity
 * @Description: TODO
 * @Author jlz
 * @Date 2020-09-07 14:10
 * @Version V1.0
 **/
@Table(name = "tb_spu")
@Data
public class SpuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    private String subTitle;

    private Integer cid1;

    private Integer cid2;

    private Integer cid3;

    private Integer brandId;

    private Integer saleable;

    private Integer valid;

    private Date createTime;

    private Date lastUpdateTime;
}
