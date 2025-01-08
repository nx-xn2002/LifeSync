package com.nx.lifesyncbackend.common;

import lombok.Getter;

/**
 * error code
 *
 * @author Ni Xiang
 * @date 2025-01-06
 */
@Getter
public enum ErrorCode {
    /**
     *
     */
    SUCCESS(0, "OK", ""),
    PARAMS_ERROR(40000, "Params Error", ""),
    NULL_ERROR(40001, "Null Error", ""),
    NOT_LOGIN(40100, "Not Login", ""),
    NO_AUTH(40101, "No Auth", ""),
    SYSTEM_ERROR(50000, "System Error", "");
    /**
     * 状态码
     */
    private final int code;
    /**
     * 提示消息
     */
    private final String message;
    /**
     * 描述
     */
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

}
