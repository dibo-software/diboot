package com.diboot.shiro.exception;

import com.diboot.core.exception.BusinessException;
import com.diboot.core.vo.Status;

/**
 * shiro自定义相关异常
 * @author : wee
 * @version : v1.0
 * @Date 2019-07-11  10:47
 */
public class ShiroCustomException extends BusinessException {

    public ShiroCustomException(Status status) {
        super(status);
    }

    public ShiroCustomException(Status status, String msg) {
        super(status, msg);
    }

}
