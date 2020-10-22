package com.baidu.shop.feign;

import com.baidu.shop.base.Result;
import com.baidu.shop.entity.UserAddressEntity;
import com.baidu.shop.service.UserAddressService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @ClassName UserFeign
 * @Description: TODO
 * @Author jlz
 * @Date 2020-10-21 20:01
 * @Version V1.0
 **/
@FeignClient(value = "user-service")
public interface UserAddressFeign {

    @GetMapping(value = "user/selectAddressById")
    Result<UserAddressEntity> selectAddressById(@RequestParam Integer id);
}
