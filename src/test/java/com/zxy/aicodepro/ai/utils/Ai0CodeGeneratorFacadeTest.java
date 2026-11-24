package com.zxy.aicodepro.ai.utils;

import com.zxy.aicodepro.ai.Ai0CodeGeneratorFacade;
import com.zxy.aicodepro.ai.enums.Ai0CodeGenTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.List;

@SpringBootTest
class Ai0CodeGeneratorFacadeTest {

    @Resource
    private Ai0CodeGeneratorFacade ai0CodeGeneratorFacade;


    @Test
    void generatorAndSaveCode() {
        ai0CodeGeneratorFacade.generatorAndSaveCode(Ai0CodeGenTypeEnum.HTML,"帮我生成一个登录页面，不超过20行代码",1L);

        ai0CodeGeneratorFacade.generatorAndSaveCode(Ai0CodeGenTypeEnum.MULTI_FILE,"帮我生成一个注册页面，不超过20行代码",1L);

    }


    @Test
    void generatorAndSaveCodeStream() {
        Flux<String> codeStream = ai0CodeGeneratorFacade.generatorAndSaveCodeStream(
                Ai0CodeGenTypeEnum.HTML,
                "帮我生成一个登录页面，不超过20行代码",
                1L);

        List<String> result = codeStream.collectList().block();
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());

        String completeContent = String.join("", result);
        Assertions.assertFalse(completeContent.isBlank());
    }
}
