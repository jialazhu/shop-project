package com.baidu.shop.utils;

/**
 * @ClassName StringUtil
 * @Description: TODO
 * @Author jlz
 * @Date 2020-08-31 19:42
 * @Version V1.0
 **/
public class StringUtil {

    public static Boolean isEmpty(String str){
        return str == null || "".equals(str);
    }
    public static Boolean isNotEmpty(String str){
        return str != null && !"".equals(str);
    }
}
