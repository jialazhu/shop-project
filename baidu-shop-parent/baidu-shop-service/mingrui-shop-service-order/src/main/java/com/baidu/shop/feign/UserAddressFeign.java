package com.baidu.shop.feign;

import com.baidu.shop.service.UserAddressService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName UserFeign
 * @Description: TODO
 * @Author jlz
 * @Date 2020-10-21 20:01
 * @Version V1.0
 **/
@FeignClient(value = "user-service")
public interface UserAddressFeign extends UserAddressService {
}
