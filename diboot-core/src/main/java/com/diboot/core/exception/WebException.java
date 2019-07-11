package com.diboot.core.exception;

import com.diboot.core.enumerate.ErrorPageEnum;

/**
 * 默认web风格异常类：系统自定义web异常可以继承该类，将会被自动捕获
 *
 * @author : wee
 * @version : v1.0
 * @Date 2019-07-11  11:11
 */
public class WebException extends RuntimeException {

    /**
     * 错误页面枚举： {@link ErrorPageEnum}
     */
    private ErrorPageEnum errorPage;

    /**
     * 错误的一些信息描述，用于设置{@link RuntimeException#getMessage()}
     */
    private String msg;

    public WebException() {
        //默认跳转400页面
        this.errorPage = ErrorPageEnum.STATUS_400;
    }

    /**
     * 自定义界面
     *
     * @param errorPage
     */
    public WebException(ErrorPageEnum errorPage) {
        this.errorPage = errorPage;
    }

    /**
     * 自定义界面: 设置自定义提示信息
     *
     * @param errorPage
     */
    public WebException(ErrorPageEnum errorPage, String msg) {
        super(msg);
        this.msg = msg;
        this.errorPage = errorPage;
    }

    public ErrorPageEnum getErrorPage() {
        return errorPage;
    }

    public void setErrorPage(ErrorPageEnum errorPage) {
        this.errorPage = errorPage;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
