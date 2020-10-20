package com.baidu.shop.constant;

/**
 * @ClassName ShopConstant
 * @Description: TODO
 * @Author jlz
 * @Date 2020-10-14 19:08
 * @Version V1.0
 **/
public class ShopConstant {

    public static final Integer USER_TYPE_USERNAME = 1; // 用户登录类型 1 用户名
    public static final Integer USER_TYPE_PHONE = 2;    // 用户登录类型 2 手机

    public static final String REDIS_PHONE_PRE = "redis_phone_";   // redis手机号 前缀
    public static final String REDIS_CAR_PRE = "redis_car_";   // redis购物车 前缀

    public static final Integer CAR_NUM_TYPE_INCREMENT = 1;  //购物车增加商品数量
    public static final Integer CAR_NUM_TYPE_DECREMENT = 2;  //购物车减少商品数量
}
