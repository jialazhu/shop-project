package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @ClassName RunEurekaServer
 * @Description: TODO
 * @Author jlz
 * @Date 2020-08-27 15:24
 * @Version V1.0
 **/
@SpringBootApplication
@EnableEurekaServer
public class RunEurekaServer {

    public static void main(String[] args) {
        SpringApplication.run(RunEurekaServer.class,args);
    }
}
