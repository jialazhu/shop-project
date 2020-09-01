package com.baidu.shop.utils;

/**
 * @ClassName StringUtil
 * @Description: TODO
 * @Author jlz
 * @Date 2020-08-31 19:42
 * @Version V1.0
 **/
public class StringUtil {

    // 不存在
    public static Boolean isEmpty(String str){
        return str == null || "".equals(str);
    }
    // 存在
    public static Boolean isNotEmpty(String str){
        return str != null && !"".equals(str);
    }
    // 转换成Integer类型
    public static Integer toInteger(String str){
        if(isNotEmpty(str)) return Integer.parseInt(str);
        return 0;
    }
}
