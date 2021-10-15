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
package com.diboot.message.utils;

import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.V;
import com.diboot.message.annotation.TemplateVariable;
import com.diboot.message.service.TemplateVariableService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 模版工具方法
 *
 * @author : uu
 * @version : v1.0
 * @Date 2021/2/19  11:05
 * @Copyright © diboot.com
 */
public class TemplateUtils {

    public static final List<String> TEMPLATE_STRATEGY_LIST = new ArrayList<>();

    /**
     * 加载所有TemplateVariableService实现类
     */
    public static List<String> loadTemplateTemplateVariableList() {
        if (V.notEmpty(TEMPLATE_STRATEGY_LIST)) {
            return TEMPLATE_STRATEGY_LIST;
        }
        TemplateVariableService templateVariableService = ContextHelper.getBean(TemplateVariableService.class);
        if (V.isEmpty(templateVariableService)) {
            return TEMPLATE_STRATEGY_LIST;
        }
        Class targetClass = BeanUtils.getTargetClass(templateVariableService);
        Method[] methods = targetClass.getDeclaredMethods();
        for (Method method : methods) {
            TemplateVariable templateVariable = method.getAnnotation(TemplateVariable.class);
            if (templateVariable == null) {
                continue;
            }
            TEMPLATE_STRATEGY_LIST.add(templateVariable.name());
            TemplateVariableService.TEMPLATE_STRATEGY_CACHE.put(templateVariable.name(), method);
        }
        return TEMPLATE_STRATEGY_LIST;
    }
}
