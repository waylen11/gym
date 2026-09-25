package com.hyw.gym.enums;

import lombok.Getter;

@Getter
public enum ResultCode {
    SUCCESS(200, "成功"),
    UNAUTHORIZED(401, "未认证"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    ERROR(500, "服务器错误");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}