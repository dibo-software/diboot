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
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.Configuration;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

/**
 * 加密拦截器
 * <p>
 * 在插入或更新之前对需要保护的字段进行加密
 *
 * @author wind
 * @version v2.3.1
 * @date 2021/08/19
 */
@Slf4j
@Component
@Intercepts({@Signature(
        type = Executor.class,
        method = "update",
        args = {MappedStatement.class, Object.class}
)})
public class EncryptInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws InvocationTargetException, IllegalAccessException {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        if (isInsertOrUpdate(mappedStatement.getSqlCommandType())) {
            Object entity = args[1];
            Configuration config = mappedStatement.getConfiguration();
            if (entity instanceof Map) {
                a:
                for (Map.Entry<?, ?> entry : ((Map<?, ?>) entity).entrySet()) {
                    Object value = entry.getValue();
                    if (null == value) {
                        continue;
                    }
                    if (!(value instanceof ArrayList)) {
                        ProtectFieldCache.encrypt(config, value);
                        break;
                    }
                    for (Object element : (ArrayList<?>) value) {
                        if (!ProtectFieldCache.encrypt(config, element)) {
                            break a;
                        }
                    }
                }
            } else {
                ProtectFieldCache.encrypt(config, entity);
            }
        }
        return invocation.proceed();
    }

    private boolean isInsertOrUpdate(SqlCommandType type) {
        return SqlCommandType.INSERT == type || SqlCommandType.UPDATE == type;
    }

    @Override
    public Object plugin(Object obj) {
        return obj instanceof Executor ? Plugin.wrap(obj, this) : obj;
    }

    @Override
    public void setProperties(Properties var1) {
    }
}
