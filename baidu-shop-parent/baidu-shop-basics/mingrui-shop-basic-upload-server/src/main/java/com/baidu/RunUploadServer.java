package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @ClassName RunUploadServer
 * @Description: TODO
 * @Author jlz
 * @Date 2020-09-01 19:11
 * @Version V1.0
 **/
@SpringBootApplication
@EnableEurekaClient
public class RunUploadServer {
    public static void main(String[] args) {
        SpringApplication.run(RunUploadServer.class,args);
    }
}
