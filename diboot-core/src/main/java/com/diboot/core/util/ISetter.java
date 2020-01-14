package com.diboot.core.util;

import java.io.Serializable;

/**
 * setter方法接口定义
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/1/17
 */
@FunctionalInterface
public interface ISetter<T, U> extends Serializable {
    void accept(T t, U u);
}