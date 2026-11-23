package com.zxy.aicodepro.ai;

import com.zxy.aicodepro.ai.AiAssistant;
import com.zxy.aicodepro.ai.enums.Ai0CodeGenTypeEnum;
import com.zxy.aicodepro.ai.model.HtmlCodeResult;
import com.zxy.aicodepro.ai.model.MultiFileCodeResult;
import com.zxy.aicodepro.ai.parser.CodeParserExecutor;
import com.zxy.aicodepro.ai.saver.CodeFileSaverExecutor;
import com.zxy.aicodepro.common.ErrorCode;
import com.zxy.aicodepro.exception.BusinessException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class Ai0CodeGeneratorFacade {

    @Resource
    private AiAssistant aiAssistant;

    public void generatorAndSaveCode(Ai0CodeGenTypeEnum type, String userMsg) {
        switch (type) {
            case HTML -> {
                HtmlCodeResult htmlCodeResult = aiAssistant.generateHtmlCode(userMsg);
//                Ai0CodeSaver.saveHtmlCode(htmlCodeResult);
                CodeFileSaverExecutor.executorSave(htmlCodeResult, Ai0CodeGenTypeEnum.HTML);
            }
            case MULTI_FILE -> {
                MultiFileCodeResult multiFileCodeResult = aiAssistant.generateMultiFileCode(userMsg);
                CodeFileSaverExecutor.executorSave(multiFileCodeResult, Ai0CodeGenTypeEnum.MULTI_FILE);
            }
            default -> {
                log.error("Unknown code generation type: {}", type);
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "Unknown code generation type");
            }
        }
    }

    public Flux<String> generatorAndSaveCodeStream(Ai0CodeGenTypeEnum type, String userMsg) {
        return switch (type) {
            case HTML -> processCodeStream(
                    aiAssistant.generateHtmlCodeStream(userMsg),
                    Ai0CodeGenTypeEnum.HTML
            );
            case MULTI_FILE -> processCodeStream(
                    aiAssistant.generateMultiFileCodeStream(userMsg),
                    Ai0CodeGenTypeEnum.MULTI_FILE
            );
            default -> {
                log.error("Unknown code generation type: {}", type);
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "Unknown code generation type");
            }
        };
    }

    /**
     * 通用流式代码处理方法
     *
     * @param codeStream  代码流
     * @param codeGenType 代码生成类型
     * @return 流式响应
     */
    private Flux<String> processCodeStream(Flux<String> codeStream, Ai0CodeGenTypeEnum codeGenType) {
        StringBuilder codeBuilder = new StringBuilder();
        // 实时收集代码片段
        return codeStream.doOnNext(codeBuilder::append).doOnComplete(() -> {
            // 流式返回完成后保存代码
            try {
                log.info("Saving code... {}", codeBuilder.toString());
                String completeCode = codeBuilder.toString();
                log.info("completeCode: {}", completeCode);
                // 使用执行器解析代码
                Object parsedResult = CodeParserExecutor.executeParser(completeCode, codeGenType);
                log.info("parsedResult: {}", parsedResult);
                // 使用执行器保存代码
                CodeFileSaverExecutor.executorSave(parsedResult, codeGenType);
            } catch (Exception e) {
                log.error("保存失败: {}", e.getMessage());
            }
        });
    }


    /**
     * 获取完整回答
     */
    public String getFullAnswer(Flux<String> flux) {
        StringBuilder text = new StringBuilder();

        flux.doOnNext(text::append)
                .blockLast();

        return text.toString();
    }

}
