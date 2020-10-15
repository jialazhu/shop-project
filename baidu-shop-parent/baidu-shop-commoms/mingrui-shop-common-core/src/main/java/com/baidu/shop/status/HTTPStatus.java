package com.baidu.shop.status;

/**
 * @program: baidu-shop-parent
 * @description:
 * @author: Mr.Zheng
 * @create: 2020-08-27 20:23
 **/
public class HTTPStatus {

    public static final int OK = 200;//成功

    public static final int ERROR = 500;//失败

    public static final int PARAMS_VALIDATE_ERROR = 5002; // 参数验证失败

    public static final int TOKEN_VALIDATE_ERROR = 5003; // token验证失败

    public static final int USER_COOKIE_VALIDATE_ERROR = 403; // 用户cookie验证失败


}
