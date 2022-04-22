package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
//@MapperScan("com.dao")
public class AccessAddApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccessAddApplication.class,args);
    }
}
