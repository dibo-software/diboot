/*
 * Copyright (c) 2015-2021, www.dibo.ltd (service@dibo.ltd).
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
import java.util.List;

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
    private static List<String> templateVariableList = new ArrayList<>();

    public static void extractVariablesFrom(List<Class<?>> variableObjectClasses) {
        if(variableObjectClasses != null) {
            for(Class<?> objClass : variableObjectClasses) {
                List<Field> fields = BeanUtils.extractFields(objClass, BindVariable.class);
                if(V.isEmpty(fields)){
                    continue;
                }
                fields.forEach( fld -> {
                    BindVariable bindVariable = fld.getAnnotation(BindVariable.class);
                    if(!templateVariableList.contains(bindVariable.name())) {
                        templateVariableList.add(bindVariable.name());
                    }
                });
            }
        }
    }

    @Override
    public List<String> getTemplateVariableList() {
        return templateVariableList;
    }

}
