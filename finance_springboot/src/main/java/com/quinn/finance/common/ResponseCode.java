package com.quinn.finance.common;

import lombok.Getter;

@Getter
public enum ResponseCode {
    OPERATION_FAILED(1001, "操作失败"),
    DATA_NOT_FOUND(1002, "数据不存在"),
    INVALID_TOKEN(1003, "无效的令牌"),
    TOKEN_EXPIRED(1004, "令牌已过期"),
    USER_NOT_FOUND(2001, "用户不存在"),
    INVALID_PASSWORD(2002, "密码错误"),
    USERNAME_EXISTS(2003, "用户名已存在"),
    INSUFFICIENT_BALANCE(3001, "余额不足"),
    INVALID_AMOUNT(3002, "无效的金额");

    private final int code;
    private final String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}