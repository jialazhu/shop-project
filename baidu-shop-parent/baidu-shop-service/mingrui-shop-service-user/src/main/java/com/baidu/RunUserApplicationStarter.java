package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @ClassName RunUserApplicationStarter
 * @Description: TODO
 * @Author jlz
 * @Date 2020-10-13 14:50
 * @Version V1.0
 **/
@SpringBootApplication
@EnableEurekaClient
@MapperScan(value = "com.baidu.shop.mapper")
public class RunUserApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(RunUserApplicationStarter.class,args);
    }
}
