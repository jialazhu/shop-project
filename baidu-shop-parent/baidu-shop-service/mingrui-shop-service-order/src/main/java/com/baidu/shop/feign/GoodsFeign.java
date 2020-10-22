package com.baidu.shop.feign;

import com.baidu.shop.service.GoodsService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName GoodsFeign
 * @Description: TODO
 * @Author jlz
 * @Date 2020-10-22 20:52
 * @Version V1.0
 **/
@FeignClient(value = "xxx-service",contextId = "GoodsFeign1")
public interface GoodsFeign extends GoodsService {
}
