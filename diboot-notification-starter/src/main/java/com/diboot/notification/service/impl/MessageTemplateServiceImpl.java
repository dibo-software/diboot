/*
 * Copyright (c) 2015-2099, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.notification.service.impl;

import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.notification.annotation.BindVariable;
import com.diboot.notification.entity.MessageTemplate;
import com.diboot.notification.mapper.MessageTemplateMapper;
import com.diboot.notification.service.MessageTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
* 消息模版相关Service实现
* @author uu
* @version v2.2.1
* @date 2021-02-18
 * @Copyright © diboot.com
*/
@Service
@Slf4j
public class MessageTemplateServiceImpl extends BaseServiceImpl<MessageTemplateMapper, MessageTemplate> implements MessageTemplateService {

    /**
     * 模板变量列表
     */
    private static Map<String, List<String>> templateVariableListCache = new LinkedHashMap<>();

    public static void extractVariablesFrom(List<Class<?>> variableObjectClasses) {
        if(variableObjectClasses != null) {
            for(Class<?> objClass : variableObjectClasses) {
                List<Field> fields = BeanUtils.extractFields(objClass, BindVariable.class);
                if(V.isEmpty(fields)){
                    continue;
                }
                templateVariableListCache.put(objClass.getSimpleName(), new ArrayList<>());
                fields.forEach( fld -> {
                    BindVariable bindVariable = fld.getAnnotation(BindVariable.class);
                    templateVariableListCache.get(objClass.getSimpleName()).add(bindVariable.name());
                });
            }
        }
    }

    @Deprecated
    @Override
    public List<String> getTemplateVariableList() {
        List<String> templateVariableList = new ArrayList<>();
        templateVariableListCache.values().forEach(templateVariableList::addAll);
        return templateVariableList;
    }

    @Override
    public Map<String, List<String>> getAllTemplateVariables() {
        return templateVariableListCache;
    }

}
