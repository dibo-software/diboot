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
package com.diboot.tenant.annotation.process;

/**
 * 租户具柄
 *
 * @author : uu
 * @version : v3.2.0
 * @Date 2023/12/18
 */
public abstract class TenantHolder {

    private static Context<String> context = new TenantContext();

    /**
     * 获取租户id
     * @return
     */
    public static String getTenantId() {
        return context.get();
    }

    /**
     * 设置租户id
     * @param tenantId
     */
    public static void setTenantId(String tenantId) {
        context.set(tenantId);
    }

    /**
     * 删除租户id
     */
    public static void removeTenantId() {
        context.remove();
    }

    /**
     * 默认使用 InheritableThreadLocal 实现上下文，可自行替换
     * @param context
     */
    public static void setContext(Context<String> context) {
        TenantHolder.context = context;
    }
}
