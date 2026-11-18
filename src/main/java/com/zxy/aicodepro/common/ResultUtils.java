package com.zxy.aicodepro.common;

public class ResultUtils {

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(200, data, "ok");
    }

    public static <T> BaseResponse<T> error(ErrorCode code) {
        return new BaseResponse<>(code);
    }

    public static <T> BaseResponse<T> error(ErrorCode code, String message) {
        return new BaseResponse<>(code.getCode(), null, message);
    }

    public static <T> BaseResponse<T> error(int code, String message) {
        return new BaseResponse<>(code, null, message);
    }

}
