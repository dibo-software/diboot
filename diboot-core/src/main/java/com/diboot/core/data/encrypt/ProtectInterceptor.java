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
package com.diboot.core.data.encrypt;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.diboot.core.binding.parser.ParserCache;
import com.diboot.core.util.S;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * 数据保护拦截器
 *
 * @author wind
 * @version v2.3.1
 * @date 2021/08/20
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})
})
public class ProtectInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        if (args.length > 1) {
            MappedStatement mappedStatement = (MappedStatement) args[0];
            SqlCommandType sqlType = mappedStatement.getSqlCommandType();
            if (SqlCommandType.INSERT == sqlType || SqlCommandType.UPDATE == sqlType) {
                Object entity = args[1];
                Configuration config = mappedStatement.getConfiguration();
                if (entity instanceof Map) {
                    for (Map.Entry<String, ?> entry : ((Map<String, ?>) entity).entrySet()) {
                        Object value = entry.getValue();
                        if (null != value && !(value instanceof Wrapper) && !S.startsWith(entry.getKey(), "param")) {
                            if (value instanceof List) {
                                for (Object obj : (List<?>) value) {
                                    if (!encryptor(config, obj, IEncryptStrategy::encrypt)) {
                                        break;
                                    }
                                }
                            } else {
                                encryptor(config, value, IEncryptStrategy::encrypt);
                            }
                        }
                    }
                } else {
                    encryptor(config, entity, IEncryptStrategy::encrypt);
                }
            }
            return invocation.proceed();
        } else {
            ResultSetHandler resultSetHandler = (ResultSetHandler) invocation.getTarget();
            Field field = resultSetHandler.getClass().getDeclaredField("mappedStatement");
            field.setAccessible(true);
            List<?> list = (List<?>) invocation.proceed();
            if (list.isEmpty()) {
                return list;
            }
            Configuration config = ((MappedStatement) field.get(resultSetHandler)).getConfiguration();
            for (Object obj : list) {
                if (!encryptor(config, obj, IEncryptStrategy::decrypt)) {
                    break;
                }
            }
            return list;
        }
    }

    @Override
    public Object plugin(Object obj) {
        return obj instanceof Executor || obj instanceof ResultSetHandler ? Plugin.wrap(obj, this) : obj;
    }

    /**
     * 数据处理
     *
     * @param config 配置
     * @param entity 对象
     * @param fun    函数
     * @return 是否进行了处理
     */
    private boolean encryptor(Configuration config, Object entity, BiFunction<IEncryptStrategy, String, String> fun) {
        Map<String, IEncryptStrategy> fieldEncryptorMap = ParserCache.getFieldEncryptorMap(entity.getClass());
        if (!fieldEncryptorMap.isEmpty()) {
            MetaObject metaObject = config.newMetaObject(entity);
            fieldEncryptorMap.forEach((k, v) -> {
                String value = S.valueOf(metaObject.getValue(k));
                metaObject.setValue(k, value == null ? null : fun.apply(v, value));
            });
        }
        return !fieldEncryptorMap.isEmpty();
    }
}
