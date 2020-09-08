package com.baidu.shop.entity;

import lombok.Data;
import sun.util.calendar.LocalGregorianCalendar;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @ClassName StockEntity
 * @Description: TODO
 * @Author jlz
 * @Date 2020-09-08 19:20
 * @Version V1.0
 **/
@Table(name = "tb_stock")
@Data
public class StockEntity {

    @Id
    private Long skuId;

    private Integer seckillStock;

    private Integer seckillTotal;

    private Integer stock;
}
