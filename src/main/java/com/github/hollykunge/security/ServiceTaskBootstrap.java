package com.github.hollykunge.security;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;

/**
 * @author: zhhongyu
 * @description: 任务管理启动类
 * @since: Create in 9:37 2019/8/27
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableEurekaClient
@EnableFeignClients
@MapperScan(basePackages = "com.github.hollykunge.security.mapper")
public class ServiceTaskBootstrap {
    public static void main(String[] args) {
       SpringApplication.run(ServiceTaskBootstrap.class, args);
    }
}
