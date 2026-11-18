package com.zxy.aicodepro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
public class ZxyAicodeProApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZxyAicodeProApplication.class, args);
    }

}
