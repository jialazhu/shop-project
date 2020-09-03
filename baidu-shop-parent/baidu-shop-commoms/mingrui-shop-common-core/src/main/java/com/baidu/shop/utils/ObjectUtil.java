package com.baidu.shop.utils;

import java.util.List;

/**
 * @ClassName ObjectUtil
 * @Description: TODO
 * @Author jlz
 * @Date 2020-08-31 14:01
 * @Version V1.0
 **/
public class ObjectUtil {

    public static Boolean isNull(Object obj){
        return null == obj;
    }

    public static Boolean isNotNull(Object obj){
        return null != obj;
    }

    public static Boolean isEmpty(List<?> obj){
        return null == obj || obj.size() == 0;
    }
    public static Boolean isNotEmpty(List<?> obj){
        return  obj != null && !obj.isEmpty();
    }
}
