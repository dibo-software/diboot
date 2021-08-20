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

import com.diboot.core.protect.annotation.ProtectField;
import com.diboot.core.protect.mask.IMaskStrategy;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 数据保护拦截器
 *
 * @author wind
 * @version v2.3.1
 * @date 2021/08/20
 */
@Slf4j
@Component
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
                    for (Map.Entry<?, ?> entry : ((Map<?, ?>) entity).entrySet()) {
                        Object value = entry.getValue();
                        if (null == value) {
                            continue;
                        }
                        if (!(value instanceof ArrayList)) {
                            encrypt(config, value);
                            break;
                        }
                        for (Object element : (ArrayList<?>) value) {
                            if (!encrypt(config, element)) {
                                return invocation.proceed();
                            }
                        }
                    }
                } else {
                    encrypt(config, entity);
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
                if (!decrypt(config, obj)) {
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

    private static final Map<Class<?>, List<FieldProtect>> CLASS_MAP = new ConcurrentHashMap<>();
    private static final Set<Class<?>> UNDESIRED = new CopyOnWriteArraySet<>();

    public List<FieldProtect> getProtectFieldMap(Class<?> clazz) {
        if (UNDESIRED.contains(clazz)) {
            return null;
        }
        List<FieldProtect> fieldProtectList = CLASS_MAP.get(clazz);
        if (fieldProtectList != null) {
            return fieldProtectList;
        }
        if (clazz.isAssignableFrom(HashMap.class)) {
            UNDESIRED.add(clazz);
        } else {
            fieldProtectList = new ArrayList<>();
            for (Field field : clazz.getDeclaredFields()) {
                ProtectField protect = field.getAnnotation(ProtectField.class);
                if (protect == null) {
                    continue;
                }
                if (!field.getType().isAssignableFrom(String.class)) {
                    throw new RuntimeException("`@ProtectField` 仅支持字符串类型。");
                }
                IEncryptStrategy encryptor = ContextHelper.getBean(protect.encryptor());
                if (encryptor == null) {
                    throw new RuntimeException("加密策略 `" + protect.encryptor().getName() + "` 未注入！");
                }
                IMaskStrategy maskStrategy = ContextHelper.getBean(protect.mask());
                if (maskStrategy == null) {
                    throw new RuntimeException("脱敏策略 `" + protect.encryptor().getName() + "` 未注入！");
                }
                fieldProtectList.add(new FieldProtect(field.getName(), encryptor, maskStrategy));
            }
            if (fieldProtectList.isEmpty()) {
                UNDESIRED.add(clazz);
            } else {
                CLASS_MAP.put(clazz, fieldProtectList);
            }
        }
        return fieldProtectList;
    }

    /**
     * 加密器处理数据
     *
     * @return 当前类型是否进行了数据处理
     */
    private boolean encryptor(Configuration config, Object entity, boolean isEncrypt) {
        List<FieldProtect> fieldProtectList = getProtectFieldMap(entity.getClass());
        if (V.notEmpty(fieldProtectList)) {
            MetaObject metaObject = config.newMetaObject(entity);
            fieldProtectList.forEach(e -> {
                String value = S.valueOf(metaObject.getValue(e.fieldName));
                if (value == null || (isEncrypt && e.maskStrategy.isMasked(value))) {
                    metaObject.setValue(e.fieldName, null);
                } else {
                    metaObject.setValue(e.fieldName, isEncrypt ? e.encryptor.encrypt(value) : e.encryptor.decrypt(value));
                }
            });
            return true;
        }
        return false;
    }

    private boolean encrypt(Configuration config, Object entity) {
        return encryptor(config, entity, true);
    }

    private boolean decrypt(Configuration config, Object entity) {
        return encryptor(config, entity, false);
    }

    @AllArgsConstructor
    private static class FieldProtect {
        String fieldName;
        IEncryptStrategy encryptor;
        IMaskStrategy maskStrategy;
    }
}
