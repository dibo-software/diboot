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
package com.diboot.message.service;

import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.message.annotation.TemplateVariable;
import com.diboot.message.entity.BaseVariableData;
import com.diboot.message.utils.TemplateUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 模版变量 + 构建Message 解析
 *
 * @author : uu
 * @version : v1.0
 * @Date 2021/2/18  18:36
 * @Copyright © diboot.com
 */
public interface TemplateVariableService {

    /**
     * 非贪婪 解析正则 ${}
     */
    Pattern TEMPLATE_VARIABLE = Pattern.compile("\\$\\{.+?\\}");

    /**
     * 变量方法缓存<{@link TemplateVariable#name()}, Method>
     */
    Map<String, Method> TEMPLATE_STRATEGY_CACHE = new HashMap<>(128);

    /**
     * 将模版内容解析成具体内容
     *
     * @param templateContent
     * @param variableData
     * @return
     */
    String parseTemplate2Content(String templateContent, BaseVariableData variableData) throws Exception;


    /**
     * 获取模版内容中的变量
     *
     * @param templateContent
     * @return
     * @throws Exception
     */
    default String[] getMessageTemplateVariables(String templateContent) throws Exception {
        // 提取变量之前校验方法缓存是否有值，无值需要重新加载
        if (V.isEmpty(TEMPLATE_STRATEGY_CACHE)) {
            TemplateUtils.loadTemplateVariableList();
        }
        // 提取模版变量并将值设置
        Matcher matcher = TEMPLATE_VARIABLE.matcher(templateContent);
        return getMatcherVariable(matcher);
    }

    /**
     * 获取匹配器 匹配的内容
     *
     * @param matcher
     * @return
     * @throws Exception
     */
    default String[] getMatcherVariable(Matcher matcher) throws Exception {
        // 提取模版中的所有变量
        List<String> templateVariableList = new ArrayList<>();
        while (matcher.find()) {
            templateVariableList.add(matcher.group());
        }
        return S.toStringArray(templateVariableList);
    }

}
