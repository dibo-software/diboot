package com.diboot.file.excel.annotation;

/**
 * 空值的处理策略
 * @author mazc@dibo.ltd
 * @version v2.1.0
 * @date 2020/07/02
 */
public enum EmptyStrategy {
    /**
     * 告警
     */
    WARN,
    /**
     * 设置为0
     */
    SET_0,
    /**
     * 忽略
     */
    IGNORE
}
