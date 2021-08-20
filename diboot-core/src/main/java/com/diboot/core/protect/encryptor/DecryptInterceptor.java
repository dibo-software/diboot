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
package com.diboot.core.protect.encryptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.Configuration;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

/**
 * 解密拦截器
 * <p>
 * 在查询返回数据时对加密字段进行解密
 *
 * @author wind
 * @version v2.3.1
 * @date 2021/08/19
 */
@Slf4j
@Component
@Intercepts({@Signature(
        type = ResultSetHandler.class,
        method = "handleResultSets",
        args = {Statement.class}
)})
public class DecryptInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        DefaultResultSetHandler target = (DefaultResultSetHandler) invocation.getTarget();
        Field field = target.getClass().getDeclaredField("mappedStatement");
        field.setAccessible(true);
        List<?> list = (List<?>) invocation.proceed();
        if (list.isEmpty()) {
            return list;
        }
        Configuration config = ((MappedStatement) field.get(target)).getConfiguration();
        for (Object obj : list) {
            if (!ProtectFieldCache.decrypt(config, obj)) {
                break;
            }
        }
        return list;
    }

    @Override
    public Object plugin(Object var1) {
        return var1 instanceof ResultSetHandler ? Plugin.wrap(var1, this) : var1;
    }

    @Override
    public void setProperties(Properties var1) {
    }
}
