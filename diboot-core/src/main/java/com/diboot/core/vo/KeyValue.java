package com.diboot.core.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * KeyValue键值对形式的VO（用于构建显示名Name-存储值Value形式的结果）
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/1/4
 */
@Getter @Setter @Accessors(chain = true)
public class KeyValue implements Serializable {
    private static final long serialVersionUID = -2358161241655186720L;

    public KeyValue(){}

    public KeyValue(String key, Object value){
        this.k = key;
        this.v = value;
    }

    /***
     * key: 显示值，需要显示的name/label文本
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

}
