package com.example.data;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableFeignClients
@EnableScheduling
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.example.data.mapper")
public class IotDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(IotDataApplication.class, args);
    }
}