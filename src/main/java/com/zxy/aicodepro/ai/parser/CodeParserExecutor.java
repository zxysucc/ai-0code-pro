package com.zxy.aicodepro.ai.parser;

import com.zxy.aicodepro.ai.enums.Ai0CodeGenTypeEnum;
import com.zxy.aicodepro.ai.model.HtmlCodeResult;
import com.zxy.aicodepro.ai.model.MultiFileCodeResult;

public class CodeParserExecutor {

    private static CodeParser<HtmlCodeResult> htmlCodeParser = new HtmlCodeParser();

    private static CodeParser<MultiFileCodeResult> multiFileCodeParser = new MultiFileCodeParser();

    public static Object executeParser(String codeContent, Ai0CodeGenTypeEnum codeGenType) {
        switch (codeGenType) {
            case HTML -> {
                return htmlCodeParser.parseCode(codeContent);
            }
            case MULTI_FILE -> {
                return multiFileCodeParser.parseCode(codeContent);
            }
            default -> {
                throw new IllegalArgumentException("Unknown code generation type: " + codeGenType);
            }
        }
    }
}
