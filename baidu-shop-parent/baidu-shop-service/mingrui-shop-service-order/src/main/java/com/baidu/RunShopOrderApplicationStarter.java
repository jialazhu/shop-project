package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @ClassName RunShopOrderApplicationStarter
 * @Description: TODO
 * @Author jlz
 * @Date 2020-10-21 14:20
 * @Version V1.0
 **/
@SpringBootApplication
@EnableFeignClients
@EnableEurekaClient
@MapperScan("com.baidu.shop.mapper")
public class RunShopOrderApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(RunShopOrderApplicationStarter.class,args);
    }
}
