package com.diboot.message.annotation;

import java.lang.annotation.*;

/**
 * 绑定变量
 * @author JerryMa
 * @version v3.0.0
 * @date 2022/5/27
 * Copyright © diboot.com
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BindVariable {

    /**
     * 绑定变量名称，如 ${用户姓名}
     *
     * @return
     */
    String name();
}