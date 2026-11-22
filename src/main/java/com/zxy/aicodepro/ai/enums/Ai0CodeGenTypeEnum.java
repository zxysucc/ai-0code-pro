package com.zxy.aicodepro.ai.enums;

import lombok.Data;
import lombok.Getter;

@Getter
public enum Ai0CodeGenTypeEnum {
    HTML("html", "HTML"),
    MULTI_FILE("multi_file", "Multi File");

    private String type;
    private String text;

    Ai0CodeGenTypeEnum(String type, String text) {
        this.type = type;
        this.text = text;
    }

    public static Ai0CodeGenTypeEnum getValue(String type) {
        for (Ai0CodeGenTypeEnum value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }
}
