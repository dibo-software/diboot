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
package com.diboot.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.message.annotation.BindVariable;
import com.diboot.message.entity.MessageTemplate;
import com.diboot.message.mapper.MessageTemplateMapper;
import com.diboot.message.service.MessageTemplateService;
import com.diboot.message.utils.TemplateUtils;
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

    @Override
    public boolean existCode(Long id, String code) {
        if (V.isEmpty(code)) {
            return false;
        }
        LambdaQueryWrapper<MessageTemplate> wrapper = Wrappers.<MessageTemplate>lambdaQuery()
                .eq(MessageTemplate::getCode, code);
        // 如果id存在，那么需要排除当前id进行查询
        if (V.notEmpty(id)) {
            wrapper.ne(MessageTemplate::getId, id);
        }
        if (V.notEmpty(getEntityList(wrapper))) {
            throw new BusinessException(Status.FAIL_OPERATION, "模版编码[" + code + "]已存在");
        }
        return false;
    }
}
