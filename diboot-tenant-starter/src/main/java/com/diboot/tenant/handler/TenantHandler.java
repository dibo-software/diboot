/*
 * Copyright (c) 2015-2024, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.tenant.handler;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.diboot.core.util.ContextHolder;
import com.diboot.iam.util.IamSecurityUtils;
import com.diboot.tenant.config.TenantProperties;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;

import java.util.Objects;

/**
 * 租户拦截器处理类
 *
 * @author : wind
 * @version : v3.2.0
 * @Date 2024/01/23
 */
public class TenantHandler implements TenantLineHandler {
    @Override
    public Expression getTenantId() {
        return new StringValue(IamSecurityUtils.getCurrentTenantId());
    }

    @Override
    public boolean ignoreTable(String tableName) {
        return Objects.requireNonNull(ContextHolder.getBean(TenantProperties.class)).isIgnoreTable(tableName);
    }
}
