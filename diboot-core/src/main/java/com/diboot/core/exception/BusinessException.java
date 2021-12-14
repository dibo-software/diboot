/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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

    private Integer code;

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
     */
    public BusinessException(Status status) {
        super(status.label());
        this.status = status;
    }

    /**
     * 自定义状态码和异常
     */
    public BusinessException(Status status, Throwable ex) {
        super(status.label(), ex);
        this.status = status;
    }

    /**
     * 自定义状态码和内容提示
     */
    public BusinessException(Status status, String msg) {
        super(status.label() + ": "+ msg);
        this.status = status;
    }

    /**
     * 自定义状态码和内容提示
     */
    public BusinessException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    /**
     * 自定义内容提示
     */
    public BusinessException(String msg) {
        super(msg);
        this.status = Status.FAIL_OPERATION;
    }

    /**
     * 自定义内容提示
     */
    public BusinessException(Status status, String msg, Throwable ex) {
        super(status.label() + ": "+ msg, ex);
        this.status = status;
    }

    /**
     * 自定义内容提示
     */
    public BusinessException(int code, String msg, Throwable ex) {
        super(msg, ex);
        this.code = code;
    }

    /**
     * 转换为Map
     */
    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>(8);
        map.put("code", getCode());
        map.put("msg", getMessage());
        return map;
    }

    /**
     * 获取status，以便复用
     */
    public Status getStatus(){
        return this.status;
    }

    private Integer getCode(){
        if(this.code == null && this.status != null){
            this.code = this.status.code();
        }
        return this.code;
    }

}
