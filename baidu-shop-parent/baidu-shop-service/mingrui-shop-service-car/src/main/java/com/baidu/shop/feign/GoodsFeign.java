package com.baidu.shop.feign;

import com.baidu.shop.service.GoodsService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName GoodsFeign
 * @Description: TODO
 * @Author jlz
 * @Date 2020-10-19 20:33
 * @Version V1.0
 **/
@FeignClient(value = "xxx-service",contextId = "GoodsFeign")
public interface GoodsFeign extends GoodsService {
}
