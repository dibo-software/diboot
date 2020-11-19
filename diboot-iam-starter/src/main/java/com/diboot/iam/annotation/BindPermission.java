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
package com.diboot.iam.annotation;

import java.lang.annotation.*;

/**
 * 绑定IAM权限 - 添加在Controller上的注解，可以支持：
 * 1. 自动将此注解code值作为前缀添加至Mapping方法上的Shiro注解的value中
 * 2. 注解自动入库，自动更新
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/23
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface BindPermission {
    /**
     * 名称
     * 设置当前权限名称
     * @return
     */
    String name();
    /**
     * 当前权限编码，默认为Entity类名
     * @return
     */
    String code() default  "";
}