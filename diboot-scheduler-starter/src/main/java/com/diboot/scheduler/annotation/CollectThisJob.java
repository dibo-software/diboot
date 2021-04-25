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
package com.diboot.scheduler.annotation;

import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 定时任务注解
 *
 * @author : uu
 * @version : v1.0
 * @Date 2020/12/1  12:49
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Lazy
public @interface CollectThisJob {

    /**
     * bean Name
     *
     * @return
     */
    @AliasFor(
            annotation = Component.class
    )
    String value() default "";

    /**
     * 是否懒加载
     *
     * @return
     */
    @AliasFor(
            annotation = Lazy.class,
            value = "value"
    )
    boolean lazy() default true;

    /**
     * 定时任务的名称
     *
     * @return
     */
    String name();

    /**
     * cron表达式
     *
     * @return
     */
    String cron() default "";

    /**
     * json格式的参数字符串
     *
     * @return
     */
    String paramJson() default "";

    /**
     * json格式class参数
     * <p>
     * 如果paramJson有值优先使用paramJson定义的值
     * <p>
     * 如果参数过多，paramJson不方便，可以定义实体类书写，并给予默认值
     *
     * @return
     */
    Class<?> paramClass() default Object.class;

}