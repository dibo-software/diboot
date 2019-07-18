package com.diboot.example.exception;

import com.diboot.core.enumerate.ErrorPageEnum;
import com.diboot.core.exception.RestException;
import com.diboot.core.exception.WebException;
import com.diboot.core.vo.Status;

/**
 * 自定义继承的rest异常
 * @author : wee
 * @version : v2.0
 * @Date 2019-07-11  15:37
 */
public class ExampleWebException extends WebException {

    public ExampleWebException() {
    }

    public ExampleWebException(ErrorPageEnum errorPage) {
        super(errorPage);
    }
}
