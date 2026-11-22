package com.zxy.aicodepro.ai;

import com.zxy.aicodepro.ai.model.HtmlCodeResult;
import com.zxy.aicodepro.ai.model.MultiFileCodeResult;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class AiAssistantTest {

    @Resource
    private AiAssistant aiAssistant;

    @Test
    void generateHtmlCode() {
        HtmlCodeResult htmlCodeResult = aiAssistant.generateHtmlCode("帮我生成一个登录页面，不超过20行代码");
        Assertions.assertNotNull(htmlCodeResult);
    }

    @Test
    void generateMultiFileCode() {
        MultiFileCodeResult multiFileCodeResult = aiAssistant.generateMultiFileCode("帮我生成一个登录页面，不超过20行代码");
        Assertions.assertNotNull(multiFileCodeResult);
    }
}