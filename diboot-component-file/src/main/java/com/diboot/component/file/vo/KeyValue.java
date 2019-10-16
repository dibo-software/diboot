package com.diboot.component.file.vo;

import java.io.Serializable;

/**
 * KeyValue键值对形式的VO
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/1/4
 */
public class KeyValue implements Serializable {
    private static final long serialVersionUID = -2358161241655186720L;

    public KeyValue(){}

    public KeyValue(String key, Object value){
        this.k = key;
        this.v = value;
    }

    /***
     * key: 显示值
     */
    private String k;

    /***
     * value: 存储值
     */
    private Object v;

    /**
     * 扩展值
     */
    private Object ext;

    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k;
    }

    public Object getV() {
        return v;
    }

    public void setV(Object v) {
        this.v = v;
    }

    public Object getExt() {
        return ext;
    }

    public void setExt(Object ext) {
        this.ext = ext;
    }
}
