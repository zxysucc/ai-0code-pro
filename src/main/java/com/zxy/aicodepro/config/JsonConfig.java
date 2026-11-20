package com.zxy.aicodepro.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Long 转 String 配置（Spring Boot 4 推荐方式）
 */
@Configuration
public class JsonConfig {

    @Bean
    public SimpleModule longToStringModule() {
        SimpleModule module = new SimpleModule();
        // Long 包装类
        module.addSerializer(Long.class, ToStringSerializer.instance);
        // long 基本类型
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);
        return module;
    }
}