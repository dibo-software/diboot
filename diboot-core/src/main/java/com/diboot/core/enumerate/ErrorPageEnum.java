package com.diboot.core.enumerate;

/**
 * 基础错误页面的枚举
 * @author : wee
 * @version v1.0
 * @Date 2019-07-11  14:17
 */
public enum ErrorPageEnum {

    STATUS_400(400, "400页面"),
    STATUS_403(403, "403页面"),
    STATUS_500(500, "500页面"),
    ;


    private int code;
    private String msg;

    ErrorPageEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
