package com.zxy.aicodepro.ai.saver;

import com.zxy.aicodepro.ai.enums.Ai0CodeGenTypeEnum;
import com.zxy.aicodepro.ai.model.MultiFileCodeResult;

public class MultiFileCodeFileSaverTemplate extends CodeFileSaverTemplate<MultiFileCodeResult> {
    @Override
    protected void saveFile(MultiFileCodeResult result, String dirPath) {
        writeToFile(result.getHtmlCode(), dirPath, "index.html");
        writeToFile(result.getCssCode(), dirPath, "style.css");
        writeToFile(result.getJsCode(), dirPath, "script.js");

    }

    @Override
    protected String getType() {
        return Ai0CodeGenTypeEnum.MULTI_FILE.getType();
    }
}
