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
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 保护字段加密算法映射缓存
 *
 * @author wind
 * @version v2.3.1
 * @date 2021/08/19
 */
public class ProtectFieldCache {

    private static final Map<Class<?>, List<FieldProtect>> CLASS_MAP = new ConcurrentHashMap<>();
    private static final Set<Class<?>> UNDESIRED = new CopyOnWriteArraySet<>();

    public static List<FieldProtect> getFieldEncyptorMap(Class<?> clazz) {
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
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                ProtectField protect = field.getAnnotation(ProtectField.class);
                if (null != protect && !field.getType().isAssignableFrom(String.class)) {
                    throw new RuntimeException("`@ProtectField` 仅支持字符串类型。");
                }
                if (null != protect) {
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
     * @return 是否进行数据处理
     */
    private static boolean encryptor(Configuration config, Object entity, boolean isEncrypt) {
        List<FieldProtect> fieldProtectList = getFieldEncyptorMap(entity.getClass());
        if (V.notEmpty(fieldProtectList)) {
            MetaObject metaObject = config.newMetaObject(entity);
            fieldProtectList.forEach(e -> {
                String value = S.valueOf(metaObject.getValue(e.fieldName));
                if (value != null || (!isEncrypt && !e.maskStrategy.isMasked(value))) {
                    metaObject.setValue(e.fieldName, isEncrypt ? e.encryptor.encrypt(value) : e.encryptor.decrypt(value));
                }
            });
            return true;
        }
        return false;
    }

    public static boolean encrypt(Configuration config, Object entity) {
        return encryptor(config, entity, true);
    }

    public static boolean decrypt(Configuration config, Object entity) {
        return encryptor(config, entity, false);
    }

    @AllArgsConstructor
    private static class FieldProtect {
        String fieldName;
        IEncryptStrategy encryptor;
        IMaskStrategy maskStrategy;
    }
}
