package com.diboot.core.exception;

import com.diboot.core.vo.Status;

/**
 * 默认rest风格异常类：系统自定义rest异常可以继承该类，将会被自动捕获
 *
 * @author : wee
 * @version : v2.0
 * @Date 2019-07-11  11:10
 */
public class RestException extends RuntimeException {

    /**
     * 错误的状态
     */
    private Status status;

    /**
     * 错误的描述
     */
    private String msg;

    /**
     * 错误的数据
     */
    private Object data;

    /**
     * 默认：操作失败
     */
    public RestException() {
        super(Status.FAIL_OPERATION.label());
        this.status = Status.FAIL_OPERATION;
    }

    /**
     * 自定义状态码
     *
     * @param status
     */
    public RestException(Status status) {
        super(status.label());
        this.status = status;
    }


    /**
     * 自定义内容提示
     *
     * @param status
     * @param msg
     */
    public RestException(Status status, String msg) {
        super(status.label() + ":" + msg);
        this.status = status;
        this.msg = msg;
    }

    /**
     * 自定义状态，带数据
     *
     * @param status
     * @param data
     */
    public RestException(Status status, Object data) {
        super(status.label());
        this.status = status;
        this.data = data;
    }

    /**
     * 自定义状态，自定义描述：带数据
     *
     * @param status
     * @param data
     * @param msg
     */
    public RestException(Status status, Object data, String msg) {
        super(status.label() + ":" + msg);
        this.status = status;
        this.msg = msg;
        this.data = data;
    }


    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
