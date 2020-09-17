package com.baidu.shop.feign;

import com.baidu.shop.service.SpecificationService;
import org.springframework.cloud.openfeign.FeignClient;

// 因为硬件原因 多个模块合在一个模块中. 需要 contextId 属性来区分. 模块名不会冲突.
@FeignClient(value = "xxx-service",contextId = "SpecificationService")
public interface SpecificationFeign extends SpecificationService {
}
