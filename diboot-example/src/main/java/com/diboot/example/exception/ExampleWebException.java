package com.diboot.example.exception;

import com.diboot.core.exception.WebException;
import org.springframework.http.HttpStatus;

/**
 * 自定义继承的rest异常
 *
 * @author : wee
 * @version : v2.0
 * @Date 2019-07-11  15:37
 */
public class ExampleWebException extends WebException {

    public ExampleWebException() {
    }

    public ExampleWebException(HttpStatus httpStatus) {
        super(httpStatus);
    }
}
