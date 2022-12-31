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
package com.diboot.core.vo;

import com.diboot.core.plugin.JsonResultFilter;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.S;
import com.diboot.core.util.V;

import java.io.Serializable;

/**
 * JSON返回结果
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/01/01
 */
@SuppressWarnings("JavaDoc")
public class JsonResult<T> implements Serializable {
    private static final long serialVersionUID = 1001L;

    /***
     * 状态码
     */
    private int code;
    /***
     * 消息内容
     */
    private String msg;
    /***
     * 返回结果数据
     */
    private T data;

    /**
     * 默认成功，无返回数据
      */
    public JsonResult(){
    }

    /**
     * 成功或失败
     */
    public JsonResult(boolean ok){
        this(ok ? Status.OK : Status.FAIL_OPERATION);
    }

    /**
     * 默认成功，有返回数据
     */
    public JsonResult(T data){
        this.code = Status.OK.code();
        this.msg = Status.OK.label();
        initMsg(null);
        this.data = data;
    }

    /**
     * 默认成功，有返回数据、及附加提示信息
     */
    public JsonResult(T data, String additionalMsg){
        this.code = Status.OK.code();
        this.msg = Status.OK.label();
        initMsg(additionalMsg);
        this.data = data;
    }

    /***
     * 非成功，指定状态
     * @param status
     */
    public JsonResult(Status status){
        this.code = status.code();
        this.msg = status.label();
        initMsg(null);
        this.data = null;
    }

    /***
     * 非成功，指定状态及附加提示信息
     * @param status
     * @param additionalMsg
     */
    public JsonResult(Status status, String additionalMsg){
        this.code = status.code();
        this.msg = status.label();
        initMsg(additionalMsg);
        this.data = null;
    }

    /**
     * 非成功，指定状态、返回数据
     * @param status
     * @param data
     */
    public JsonResult(Status status, T data){
        this.code = status.code();
        this.msg = status.label();
        initMsg(null);
        this.data = data;
    }

    /**
     * 非成功，指定状态、返回数据、及附加提示信息
      */
    public JsonResult(Status status, T data, String additionalMsg){
        this.code = status.code();
        this.msg = status.label();
        initMsg(additionalMsg);
        this.data = data;
    }

    /***
     * 自定义JsonResult
     * @param code
     * @param label
     * @param data
     */
    public JsonResult(int code, String label, T data){
        this.code = code;
        this.msg = label;
        this.data = data;
    }

    /**
     * 设置status，如果msg为空则msg设置为status.label
     * @param status
     * @return
     */
    public JsonResult<T> status(Status status){
        this.code = status.code();
        if(this.msg == null){
            this.msg = status.label();
        }
        return this;
    }

    /**
     * 设置返回数据
     * @param data
     * @return
     */
    public JsonResult<T> data(T data){
        this.data = data;
        return this;
    }

    /**
     * 设置msg
     * @param additionalMsg
     * @return
     */
    public JsonResult<T> msg(String additionalMsg){
        initMsg(additionalMsg);
        return this;
    }

    public int getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
    public T getData() {
        return filterJsonResultData(data);
    }
    public void setCode(int code) {
        this.code = code;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public void setData(T data) {
        this.data = data;
    }

    /***
     * 绑定分页信息
     * @param pagination
     */
    @SuppressWarnings("unchecked")
    public PagingJsonResult<T> bindPagination(Pagination pagination){
        return new PagingJsonResult(this, pagination);
    }

    /**
     * 赋值msg（去掉重复前缀以支持与BusinessException嵌套使用）
     * @param additionalMsg
     */
    private void initMsg(String additionalMsg){
        if(V.notEmpty(additionalMsg)){
            if(S.startsWith(additionalMsg, this.msg)){
                this.msg = additionalMsg;
            }
            else{
                this.msg += ": " + additionalMsg;
            }
        }
    }

    /**
     * 判断结果是否OK
     * @return
     */
    public boolean isOK(){
        return this.code == Status.OK.code();
    }

    /***
     * 请求处理成功
     */
    public static <T> JsonResult<T> OK(){
        return new JsonResult<>(Status.OK);
    }
    /***
     * 请求处理成功
     */
    public static <T> JsonResult<T> OK(T data){
        return new JsonResult<>(Status.OK, data);
    }

    /***
     * 部分成功（一般用于批量处理场景，只处理筛选后的合法数据）
     */
    public static <T> JsonResult<T> WARN_PARTIAL_SUCCESS(String msg){
        return new JsonResult<T>(Status.WARN_PARTIAL_SUCCESS).msg(msg);
    }
    /***
     * 有潜在的性能问题
     */
    public static <T> JsonResult<T> WARN_PERFORMANCE_ISSUE(String msg){
        return new JsonResult<T>(Status.WARN_PERFORMANCE_ISSUE).msg(msg);
    }
    /***
     * 传入参数不对
     */
    public static <T> JsonResult<T> FAIL_INVALID_PARAM(String msg){
        return new JsonResult<T>(Status.FAIL_INVALID_PARAM).msg(msg);
    }
    /***
     * Token无效或已过期
     */
    public static <T> JsonResult<T> FAIL_INVALID_TOKEN(String msg){
        return new JsonResult<T>(Status.FAIL_INVALID_TOKEN).msg(msg);
    }
    /***
     * 没有权限执行该操作
     */
    public static <T> JsonResult<T> FAIL_NO_PERMISSION(String msg){
        return new JsonResult<T>(Status.FAIL_NO_PERMISSION).msg(msg);
    }
    /***
     * 请求资源不存在
     */
    public static <T> JsonResult<T> FAIL_NOT_FOUND(String msg){
        return new JsonResult<T>(Status.FAIL_NOT_FOUND).msg(msg);
    }
    /***
     * 数据校验不通过
     */
    public static <T> JsonResult<T> FAIL_VALIDATION(String msg){
        return new JsonResult<T>(Status.FAIL_VALIDATION).msg(msg);
    }
    /***
     * 操作执行失败
     */
    public static <T> JsonResult<T> FAIL_OPERATION(String msg){
        return new JsonResult<T>(Status.FAIL_OPERATION).msg(msg);
    }
    /***
     * 系统异常
     */
    public static <T> JsonResult<T> FAIL_EXCEPTION(String msg){
        return new JsonResult<T>(Status.FAIL_EXCEPTION).msg(msg);
    }
    /***
     * 服务不可用
     */
    public static <T> JsonResult<T> FAIL_REQUEST_TIMEOUT(String msg){
        return new JsonResult<T>(Status.FAIL_REQUEST_TIMEOUT).msg(msg);
    }
    /***
     * 服务不可用
     */
    public static <T> JsonResult<T> FAIL_SERVICE_UNAVAILABLE(String msg){
        return new JsonResult<T>(Status.FAIL_SERVICE_UNAVAILABLE).msg(msg);
    }

    /***
     * 认证不通过
     */
    public static <T> JsonResult<T> FAIL_AUTHENTICATION(String msg){
        return new JsonResult<T>(Status.FAIL_AUTHENTICATION).msg(msg);
    }

    /**
     * 过滤jsonResult结果，用于全局忽略某些字段等场景
     * @param data
     * @param <T>
     * @return
     */
    private static boolean jsonResultFilterChecked = false;
    private static JsonResultFilter jsonResultFilter;
    private static <T> T filterJsonResultData(T data){
        // 不启用过滤
        if(jsonResultFilterChecked && jsonResultFilter == null){
            return data;
        }
        if(!jsonResultFilterChecked){
            jsonResultFilter = ContextHelper.getBean(JsonResultFilter.class);
            jsonResultFilterChecked = true;
        }
        if(jsonResultFilter != null){
            jsonResultFilter.filterData(data);
        }
        return data;
    }
}
