package com.arrend_system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.arrend_system.mapper")
public class ErrandSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ErrandSystemApplication.class, args);
    }

}
