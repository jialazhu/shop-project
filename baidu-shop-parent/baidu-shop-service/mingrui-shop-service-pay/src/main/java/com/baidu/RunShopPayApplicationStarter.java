package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @ClassName RunShopPayApplicationStarter
 * @Description: TODO
 * @Author jlz
 * @Date 2020-10-22 15:05
 * @Version V1.0
 **/
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableEurekaClient
@EnableFeignClients
public class RunShopPayApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(RunShopPayApplicationStarter.class,args);
    }
}
