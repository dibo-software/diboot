package com.diboot.core.vo;

import com.diboot.core.util.S;
import com.diboot.core.util.V;

import java.io.Serializable;

/**
 * JSON返回结果
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/01/01
 */
public class JsonResult implements Serializable {
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
    private Object data;

    /**
     * 默认成功，无返回数据
      */
    public JsonResult(){
    }

    /**
     * 默认成功，有返回数据
     */
    public JsonResult(Object data){
        this.code = Status.OK.code();
        this.msg = Status.OK.label();
        initMsg(null);
        this.data = data;
    }

    /**
     * 默认成功，有返回数据、及附加提示信息
     */
    public JsonResult(Object data, String additionalMsg){
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
    public JsonResult(Status status, Object data){
        this.code = status.code();
        this.msg = status.label();
        initMsg(null);
        this.data = data;
    }

    /**
     * 非成功，指定状态、返回数据、及附加提示信息
      */
    public JsonResult(Status status, Object data, String additionalMsg){
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
    public JsonResult(int code, String label, Object data){
        this.code = code;
        this.msg = label;
        this.data = data;
    }

    /**
     * 设置status，如果msg为空则msg设置为status.label
     * @param status
     * @return
     */
    public JsonResult status(Status status){
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
    public JsonResult data(Object data){
        this.data = data;
        return this;
    }

    /**
     * 设置msg
     * @param additionalMsg
     * @return
     */
    public JsonResult msg(String additionalMsg){
        initMsg(additionalMsg);
        return this;
    }

    /***
     * 绑定分页信息
     * @param pagination
     */
    public JsonResult bindPagination(Pagination pagination){
        return new PagingJsonResult(this, pagination);
    }

    public int getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
    public Object getData() {
        return data;
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

}