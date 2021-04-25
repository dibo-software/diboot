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

import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.message.annotation.TemplateVariable;
import com.diboot.message.entity.BaseVariableData;
import com.diboot.message.service.TemplateVariableService;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统模版默认实现
 *
 * @author : uu
 * @version : v1.0
 * @Date 2021/2/18  19:25
 * @Copyright © diboot.com
 */
@Slf4j
public class SystemTemplateVariableServiceImpl implements TemplateVariableService {

    @Override
    public String parseTemplate2Content(String templateContent, BaseVariableData baseVariableData) throws Exception {
        // 提取模版中使用到的模版变量
        String[] messageTemplateVariables = getMessageTemplateVariables(templateContent);
        // 从缓存中获取变量对应的方法
        List<String> variableValueList = new ArrayList<>();
        for (String variable : messageTemplateVariables) {
            Method method = TEMPLATE_STRATEGY_CACHE.get(variable);
            // 方法不存在，则当前变量无法替换，那么使用空字符串
            if (method == null) {
                log.warn("【提取变量方法失败】：当前变量为：{}", variable);
                variableValueList.add("");
                continue;
            }
            // 执行方法，获取变量值
            Object value = method.invoke(this, baseVariableData);
            if (value != null) {
                variableValueList.add(String.valueOf(value));
            } else {
                log.warn("【执行方法获取内容为空】：当前变量为：{}", variable);
                variableValueList.add("");
            }
        }
        return S.replaceEach(templateContent, messageTemplateVariables, S.toStringArray(variableValueList));
    }

    /**
     * 用户名变量
     *
     * @param baseVariableData
     * @return
     */
    @TemplateVariable(name = "${用户姓名}")
    private String getRealName(BaseVariableData baseVariableData) {
        return V.notEmpty(baseVariableData.getRealName()) ? baseVariableData.getRealName() : "";
    }

    /**
     * 手机号变量
     *
     * @param baseVariableData
     * @return
     */
    @TemplateVariable(name = "${手机号}")
    private String getMobilePhone(BaseVariableData baseVariableData) {
        return V.notEmpty(baseVariableData.getPhone()) ? baseVariableData.getPhone() : "";
    }
}
