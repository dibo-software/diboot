/*
 * Copyright (c) 2015-2099, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.core.binding.query;

import javax.lang.model.type.NullType;
import java.lang.annotation.*;

/**
 * 绑定管理器
 *
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/7/18
 */
@Target(ElementType.FIELD)
@Repeatable(BindQuery.List.class)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BindQuery {

    /**
     * 查询条件
     */
    Comparison comparison() default Comparison.EQ;

    /**
     * 数据库字段，默认为空，自动根据驼峰转下划线
     */
    @Deprecated
    String column() default "";

    /**
     * entity字段名，不指定默认为当前Entity同名属性名
     * @return
     */
    String field() default "";

    /**
     * 绑定的Entity类
     */
    Class<?> entity() default NullType.class;

    /**
     * 查询是否需要去重
     * @return
     */
    boolean distinct() default true;

    /**
     * JOIN连接条件，支持动态的跨表JOIN查询
     */
    String condition() default "";

    /**
     * 构建查询条件时忽略该字段
     */
    boolean ignore() default false;

    /**
     * 忽略select查询该字段，用于构建查询时剔除Entity中的大字段，避免Oracle等构建DISTINCT查询时报错
     * @return
     */
    boolean ignoreSelect() default false;

    /**
     * 查询处理策略：默认忽略空字符串
     */
    Strategy strategy() default Strategy.IGNORE_EMPTY;

    /**
     * 在同一个字段上支持多个{@link BindQuery}，之间会用采用OR的方式连接
     *
     * @author wind
     * @since v2.4.0
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {

        BindQuery[] value();

    }

}
