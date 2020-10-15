package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @ClassName RunUserOauthStarter
 * @Description: TODO
 * @Author jlz
 * @Date 2020-10-15 14:09
 * @Version V1.0
 **/
@SpringBootApplication
@EnableEurekaClient
@MapperScan(value = "com.baidu.shop.mapper")
public class RunUserOauthStarter {

    public static void main(String[] args) {
        SpringApplication.run(RunUserOauthStarter.class,args);
    }
}
