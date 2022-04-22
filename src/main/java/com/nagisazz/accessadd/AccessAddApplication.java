package com.nagisazz.accessadd;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.nagisazz.accessadd.dao")
public class AccessAddApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccessAddApplication.class,args);
    }
}
