package com.zxy.aicodepro.ai.saver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.zxy.aicodepro.common.ErrorCode;
import com.zxy.aicodepro.exception.ThrowUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CodeFileSaverTemplate<T> {

    public static final String FILE_SAVER_ROOT_SAVER = System.getProperty("user.dir") + "/tmp/code_output";


    public void saveCode(T result) {
        invalidInput(result);
        saveFile(result, buildUniqueDir());
    }

    protected void invalidInput(T result) {
        ThrowUtils.throwIf(result == null, ErrorCode.PARAMS_ERROR, "Result cannot be null");
    }

    protected abstract void saveFile(T result, String dirPath);

    protected abstract String getType();


    protected String buildUniqueDir() {
        String type = getType();
        String uniqueDir = StrUtil.format("{}_{}", type, IdUtil.getSnowflakeNextId());
        log.info("Creating unique directory: {}", uniqueDir);
        String dirPath = FILE_SAVER_ROOT_SAVER + "/" + uniqueDir;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    protected void writeToFile(String content, String path, String fileName) {
        if (content == null || content.isEmpty()) {
            log.info("Content is empty, not writing to file.");
            return;
        }
        String filePath = path + "/" + fileName;
        FileUtil.writeString(content, filePath, "utf-8");
    }

}
