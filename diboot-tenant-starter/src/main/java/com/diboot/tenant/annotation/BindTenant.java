/*
 * Copyright (c) 2015-2023, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.tenant.annotation;

import com.diboot.tenant.annotation.process.TenantContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 如果接口中需要使用租户信息，加上此注解
 * <p>
 * 请求开始时自动调用{@link TenantContext#set(String)}设置租户id
 * <p>
 * 请求中 可以手动调用 {@link TenantContext#get()}获取租户id
 * <p>
 * 请求结束自动调用 {@link TenantContext#remove()}销毁当前请求的租户数据
 *
 * @author : uu
 * @version : v3.2.0
 * @Date 2023/12/18
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface BindTenant {
}
