package com.zxy.aicodepro.ai.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.zxy.aicodepro.ai.enums.Ai0CodeGenTypeEnum;
import com.zxy.aicodepro.ai.model.HtmlCodeResult;
import com.zxy.aicodepro.ai.model.MultiFileCodeResult;
import lombok.extern.slf4j.Slf4j;

@Deprecated
@Slf4j
public class Ai0CodeSaver {
    public static final String FILE_SAVER_ROOT_SAVER = System.getProperty("user.dir") + "/tmp/code_output";

    public static void saveHtmlCode(HtmlCodeResult result) {
        String path = buildUniqueDir(Ai0CodeGenTypeEnum.HTML.getType());
        writeToFile(result.getHtmlCode(), path, "index.html");
    }


    public static void saveMultiFileCode(MultiFileCodeResult result) {
        String path = buildUniqueDir(Ai0CodeGenTypeEnum.MULTI_FILE.getType());
        writeToFile(result.getHtmlCode(), path, "index.html");
        writeToFile(result.getCssCode(), path, "style.css");
        writeToFile(result.getJsCode(), path, "script.js");
    }
    public static void writeToFile(String content, String path, String fileName) {
        if(content == null || content.isEmpty()) {
            log.info("Content is empty, not writing to file.");
            return;
        }
        String filePath = path + "/" + fileName;
        FileUtil.writeString(content, filePath, "utf-8");
    }

    public static String buildUniqueDir(String type) {
        String uniqueDir = StrUtil.format("{}_{}", type, IdUtil.getSnowflakeNextId());
        log.info("Creating unique directory: {}", uniqueDir);
        String dirPath = FILE_SAVER_ROOT_SAVER + "/" + uniqueDir;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }
}
