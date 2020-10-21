package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_order_status")
@Data
public class OrderStatusEntity {

    @Id
    private Long orderId; //订单id

    private Integer status; //订单状态: 1、未付款 2、已付款,未发货 3、已发货,未确认 4、交易成功 5、交易关闭 6、已评价'

    private Date createTime; //创建时间

    private Date paymentTime; //付款时间

}