package com.diboot.core.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * API接口地址
 * @author JerryMa
 * @version v2.6.0
 * @date 2022/4/21
 * Copyright © diboot.com
 */
@Getter
@Setter
public class ApiUri implements Serializable {
    private static final long serialVersionUID = 662520281067250572L;

    public ApiUri() {
    }

    public ApiUri(String method, String uri){
        this.method = method;
        this.uri = uri;
    }

    public ApiUri(String method, String uri, String label){
        this.method = method;
        this.uri = uri;
        this.label = label;
    }

    /**
     * 接口Method
     */
    private String method;

    /**
     * 接口URI
     */
    private String uri;

    /**
     * 接口备注
     */
    private String label;

    @JsonIgnore
    public boolean isEmpty(){
        return method == null || uri == null;
    }
}
