package com.diboot.core.exception;

import com.diboot.core.vo.Status;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用的业务异常类 BusinessException
 * (json形式返回值同JsonResult，便于前端统一处理)
 * @author : wee
 * @version : v2.0
 * @Date 2019-07-11  11:10
 */
public class BusinessException extends RuntimeException {

    /**
     * 错误的状态
     */
    private Status status;

    /**
     * 默认：操作失败
     */
    public BusinessException() {
        super(Status.FAIL_OPERATION.label());
        this.status = Status.FAIL_OPERATION;
    }

    /**
     * 自定义状态码
     * @param status
     */
    public BusinessException(Status status) {
        super(status.label());
        this.status = status;
    }

    /**
     * 自定义状态码和异常
     * @param status
     */
    public BusinessException(Status status, Throwable ex) {
        super(status.label(), ex);
        this.status = status;
    }

    /**
     * 自定义状态码和内容提示
     * @param status
     * @param msg
     */
    public BusinessException(Status status, String msg) {
        super(status.label() + ": "+ msg);
        this.status = status;
    }

    /**
     * 自定义内容提示
     * @param msg
     */
    public BusinessException(String msg) {
        super( msg);
    }

    /**
     * 自定义内容提示
     * @param status
     * @param msg
     */
    public BusinessException(Status status, String msg, Throwable ex) {
        super(status.label() + ": "+ msg, ex);
        this.status = status;
    }

    /**
     * 转换为Map
     * @return
     */
    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("code", status.code());
        map.put("msg", getMessage());
        return map;
    }

    /**
     * 获取status，以便复用
     * @return
     */
    public Status getStatus(){
        return this.status;
    }
}
