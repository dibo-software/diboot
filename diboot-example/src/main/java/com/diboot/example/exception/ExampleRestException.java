package com.diboot.example.exception;

import com.diboot.core.exception.RestException;
import com.diboot.core.vo.Status;

/**
 * 自定义继承的rest异常
 *
 * @author : wee
 * @version : v2.0
 * @Date 2019-07-11  15:37
 */
public class ExampleRestException extends RestException {

    /**
     * 默认：操作失败
     */
    public ExampleRestException() {
    }

    /**
     * 自定义状态码
     *
     * @param status
     */
    public ExampleRestException(Status status) {
        super(status);
    }

    /**
     * 自定义内容提示
     *
     * @param status
     * @param msg
     */
    public ExampleRestException(Status status, String msg) {
        super(status, msg);
    }

    /**
     * 自定义状态，带数据
     *
     * @param status
     * @param data
     */
    public ExampleRestException(Status status, Object data) {
        super(status, data);
    }

    /**
     * 自定义状态，自定义描述：带数据
     *
     * @param status
     * @param data
     * @param msg
     */
    public ExampleRestException(Status status, Object data, String msg) {
        super(status, data, msg);
    }
}
