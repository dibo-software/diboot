package com.diboot.core.config;

/**
 * 基础常量定义
 * @author Mazhicheng
 * @version 2.0
 * @date 2019/01/01
 */
public class Cons {
    /**
     * 默认字符集UTF-8
     */
    public static final String CHARSET_UTF8 = "UTF-8";
    /**
     * 分隔符 ,
     */
    public static final String SEPARATOR_COMMA = ",";

    public static final String SEPARATOR_UNDERSCORE = "_";
    /***
     * 默认字段名定义
     */
    public enum FieldName{
        /**
         * 主键属性名
         */
        id,
        /**
         * 默认的上级ID属性名
         */
        parentId,
        /**
         * 子节点属性名
         */
        children
    }

}
