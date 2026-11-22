package com.zxy.aicodepro.ai.parser;

public interface CodeParser<T> {

    T parseCode(String content);
}
