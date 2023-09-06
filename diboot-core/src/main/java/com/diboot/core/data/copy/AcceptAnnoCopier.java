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
package com.diboot.core.data.copy;

import com.diboot.core.config.Cons;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.beans.BeanWrapper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Accept注解拷贝器
 * @author mazc@dibo.ltd
 * @version v2.1
 * @date 2020/06/04
 */
@Slf4j
public class AcceptAnnoCopier {
    /**
     * 注解缓存
     */
    private static final Map<String, List<CopyInfo>> CLASS_ACCEPT_ANNO_CACHE_MAP = new ConcurrentHashMap<>();

    /**
     * 基于注解拷贝属性
     * @param source
     * @param target
     */
    public static void copyAcceptProperties(Object source, Object target){
        String key = target.getClass().getName();
        // 初始化
        if(!CLASS_ACCEPT_ANNO_CACHE_MAP.containsKey(key)){
            List<Field> annoFieldList = BeanUtils.extractFields(target.getClass(), Accept.class);
            if(V.isEmpty(annoFieldList)){
                CLASS_ACCEPT_ANNO_CACHE_MAP.put(key, Collections.EMPTY_LIST);
            }
            else{
                List<CopyInfo> annoDefList = new ArrayList<>(annoFieldList.size());
                for(Field fld : annoFieldList){
                    Accept accept = fld.getAnnotation(Accept.class);
                    CopyInfo copyInfo = new CopyInfo(accept.name(), fld.getName(), accept.override());
                    annoDefList.add(copyInfo);
                }
                CLASS_ACCEPT_ANNO_CACHE_MAP.put(key, annoDefList);
            }
        }
        // 解析copy
        List<CopyInfo> acceptAnnos = CLASS_ACCEPT_ANNO_CACHE_MAP.get(key);
        if(V.isEmpty(acceptAnnos)){
            return;
        }
        BeanWrapper beanWrapper = BeanUtils.getBeanWrapper(target);
        for(CopyInfo copyInfo : acceptAnnos){
            if(!copyInfo.isOverride()){
                Object targetValue = BeanUtils.getProperty(target, copyInfo.getTo());
                if(targetValue != null){
                    log.debug("目标对象{}已有值{}，copyAcceptProperties将忽略.", key, targetValue);
                    continue;
                }
            }
            Object sourceValue = SystemMetaObject.forObject(source).getValue(copyInfo.getForm());
            if(sourceValue != null) {
                beanWrapper.setPropertyValue(copyInfo.getTo(), sourceValue);
            }
        }
    }

}
