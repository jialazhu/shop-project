package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @ClassName RunShopCarApplicationStarter
 * @Description: TODO
 * @Author jlz
 * @Date 2020-10-19 19:44
 * @Version V1.0
 **/
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableEurekaClient
@EnableFeignClients
public class RunShopCarApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(RunShopCarApplicationStarter.class,args);
    }
}
