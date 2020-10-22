package com.baidu.shop.dto;

import com.baidu.shop.entity.OrderDetailEntity;
import com.baidu.shop.entity.OrderStatusEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @ClassName OrderInfo
 * @Description: TODO
 * @Author jlz
 * @Date 2020-10-22 19:25
 * @Version V1.0
 **/
@Data
@ApiModel(value = "订单详情")
public class OrderInfo {

    private Long orderId;//订单id

    private Long totalPay;//总金额,单位为分

    private Long actualPay;//实付金额,有活动或者优惠的话可能会与实际金额不一直

    private String promotionIds;//促销/活动的id集合

    private Integer paymentType;//支付类型

    private Date createTime;//订单生成时间

    private String userId;//用户id,可以换成与用户表一直的数据类型

    private String buyerMessage;//买家留言

    private String buyerNick; //买家昵称

    private Integer buyerRate;//买家是否已经评价

    private Integer invoiceType;//发票类型;

    private Integer sourceType;//订单来源

    private String receiverState; //收货地址(省)

    private String receiverCity;//收货地址(市)

    private String receiverDistrict;//收货地址(区/县)

    private String receiverAddress;//收货地址(详细地址)

    private String receiverMobile;//收货人手机

    private String receiverZip;//收货人邮编

    private String receiver;//收货人

    private List<OrderDetailEntity> OrderDetailList;

    private OrderStatusEntity orderStatusEntity;
}
