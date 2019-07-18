package com.diboot.core.exception;

import org.springframework.http.HttpStatus;

/**
 * 默认web风格异常类：系统自定义web异常可以继承该类，将会被自动捕获
 *
 * @author : wee
 * @version : v1.0
 * @Date 2019-07-11  11:11
 */
public class WebException extends RuntimeException {

    /**
     * 错误页面枚举： {@link HttpStatus}
     */
    private HttpStatus httpStatus;

    /**
     * 错误的一些信息描述，用于设置{@link RuntimeException#getMessage()}
     */
    private String msg;

    public WebException() {
        //默认跳转400页面
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    /**
     * 自定义界面
     *
     * @param httpStatus
     */
    public WebException(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    /**
     * 自定义界面: 设置自定义提示信息
     *
     * @param httpStatus
     */
    public WebException(HttpStatus httpStatus, String msg) {
        super(msg);
        this.msg = msg;
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
