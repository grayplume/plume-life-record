package com.plume.plrtime;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.plume.plrtime.mapper")
public class PlrTimeApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlrTimeApplication.class, args);
    }

}
