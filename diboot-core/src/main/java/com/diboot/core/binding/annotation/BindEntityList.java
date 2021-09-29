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
package com.diboot.core.binding.annotation;

import java.lang.annotation.*;

/**
 * 绑定Entity集合注解（1-n）
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/1/21
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface BindEntityList {
    /***
     * 对应的entity类
     * @return
     */
    Class entity();

    /***
     * JOIN连接条件
     * @return
     */
    String condition();

    /**
     * 深度绑定
     * @return
     */
    boolean deepBind() default false;

    /**
     * EntityList排序，示例 `id:DESC,age:ASC`
     * @return
     */
    String orderBy() default "";

    /**
     * 分隔符，用于拆解拼接存储的多个id值
     * @return
     */
    String splitBy() default "";
}