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

/**
 * 状态码定义
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/01/01
 */
public enum Status {
    /***
     * 请求处理成功
     */
    OK(0, "操作成功"),

    /***
     * 部分成功（一般用于批量处理场景，只处理筛选后的合法数据）
     */
    WARN_PARTIAL_SUCCESS(1001, "部分成功"),

    /***
     * 有潜在的性能问题
     */
    WARN_PERFORMANCE_ISSUE(1002, "潜在的性能问题"),

    /***
     * 传入参数不对
     */
    FAIL_INVALID_PARAM(4000, "请求参数不匹配"),

    /***
     * Token无效或已过期
     */
    FAIL_INVALID_TOKEN(4001, "Token无效或已过期"),

    /***
     * 没有权限执行该操作
     */
    FAIL_NO_PERMISSION(4003, "无权执行该操作"),

    /***
     * 请求资源不存在
     */
    FAIL_NOT_FOUND(4004, "请求资源不存在"),

    /***
     * 数据校验不通过
     */
    FAIL_VALIDATION(4005, "数据校验不通过"),

    /***
     * 操作执行失败
     */
    FAIL_OPERATION(4006, "操作执行失败"),

    /***
     * 系统异常
     */
    FAIL_EXCEPTION(5000, "系统异常"),

    /***
     * 缓存清空
     */
    MEMORY_EMPTY_LOST(9999, "缓存清空");

    private int code;
    private String label;
    Status(int code, String label){
        this.code = code;
        this.label = label;
    }
    public int code(){
        return this.code;
    }
    public String label(){
        return this.label;
    }
    public static int getCode(String value){
        for(Status eu : Status.values()){
            if(eu.name().equals(value)){
                return eu.code();
            }
        }
        return 0;
    }
    public static String getLabel(String value){
        for(Status eu : Status.values()){
            if(eu.name().equals(value)){
                return eu.label();
            }
        }
        return null;
    }

}