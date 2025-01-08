package com.nx.lifesyncbackend.common.utils;

import com.nx.lifesyncbackend.common.BaseResponse;
import com.nx.lifesyncbackend.common.ErrorCode;

/**
 * 结果工具类
 *
 * @author Ni Xiang
 */
public class ResultUtils {
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<T>(0, data, "OK");
    }

    public static <T> BaseResponse<T> error(ErrorCode errorCode) {
        return new BaseResponse<T>(errorCode);
    }

    public static <T> BaseResponse<T> error(int code, String message, String description) {
        return new BaseResponse<>(code, null, message, description);
    }
}
