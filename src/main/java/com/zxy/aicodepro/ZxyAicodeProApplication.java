package com.zxy.aicodepro;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("com.zxy.aicodepro.mapper")
public class ZxyAicodeProApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZxyAicodeProApplication.class, args);
    }

}
