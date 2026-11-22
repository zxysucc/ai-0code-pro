package com.zxy.aicodepro.ai.saver;

import com.zxy.aicodepro.ai.enums.Ai0CodeGenTypeEnum;
import com.zxy.aicodepro.ai.model.HtmlCodeResult;

public class HtmlCodeFileSaverTemplate extends CodeFileSaverTemplate<HtmlCodeResult> {
    @Override
    protected void saveFile(HtmlCodeResult result, String dirPath) {
        writeToFile(result.getHtmlCode(), dirPath, "index.html");
    }

    @Override
    protected String getType() {
        return Ai0CodeGenTypeEnum.HTML.getType();
    }
}
