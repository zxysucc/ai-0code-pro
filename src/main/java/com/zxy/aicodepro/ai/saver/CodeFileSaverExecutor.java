package com.zxy.aicodepro.ai.saver;

import com.zxy.aicodepro.ai.enums.Ai0CodeGenTypeEnum;
import com.zxy.aicodepro.ai.model.HtmlCodeResult;
import com.zxy.aicodepro.ai.model.MultiFileCodeResult;

public class CodeFileSaverExecutor {

    private static final CodeFileSaverTemplate<HtmlCodeResult> htmlCodeFileSaverTemplate = new HtmlCodeFileSaverTemplate();


    private static final CodeFileSaverTemplate<MultiFileCodeResult> multiFileCodeFileSaverTemplate = new MultiFileCodeFileSaverTemplate();

    public static void executorSave(Object result, Ai0CodeGenTypeEnum type) {
        switch (type) {
            case HTML -> {
                htmlCodeFileSaverTemplate.saveCode((HtmlCodeResult)result);
            }
            case MULTI_FILE -> {
                multiFileCodeFileSaverTemplate.saveCode((MultiFileCodeResult)result );
            }
            default -> {
                throw new IllegalArgumentException("Invalid type: " + type);
            }
        }

    }
}
