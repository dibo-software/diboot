package com.diboot.commons.config;

public class BaseCons {

    /**
     * 默认字符集UTF-8
     */
    public static final String CHARSET_UTF8 = "UTF-8";
    /***
     * ISO-8859-1
     */
    public static final String CHARSET_ISO8859_1 = "ISO8859-1";

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
         * 新建时间属性名
         */
        create_time,
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
