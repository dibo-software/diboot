package com.diboot.core.binding.query;

/**
 * 比较条件枚举类
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/08/06
 */
public enum Comparison {
    EQ, // 相等，默认
    IN, // IN

    STARTSWITH, //以xx起始

    LIKE, // LIKE
    CONTAINS, //包含，等同LIKE

    GT, // 大于
    GE, // 大于等于
    LT, // 小于
    LE, // 小于等于

    BETWEEN, //介于-之间
    BETWEEN_BEGIN, //介于之后
    BETWEEN_END //介于之前
}
