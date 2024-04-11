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
package com.diboot.notification.utils;

import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.notification.annotation.BindVariable;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 模版工具方法
 *
 * @author : uu
 * @version : v1.0
 * @Date 2021/2/19  11:05
 * @Copyright © diboot.com
 */
@Slf4j
public class TemplateUtils {

    /**
     * 非贪婪 解析正则 ${}
     */
    private static Pattern TEMPLATE_VARIABLE = Pattern.compile("\\$\\{.+?\\}");

    /**
     * 获取模版内容中的变量
     *
     * @param templateContent
     * @return
     * @throws Exception
     */
    private static List<String> getMessageTemplateVariables(String templateContent) throws Exception {
        // 提取模版变量并将值设置
        Matcher matcher = TEMPLATE_VARIABLE.matcher(templateContent);
        // 提取模版中的所有变量
        List<String> templateVariableList = new ArrayList<>();
        while (matcher.find()) {
            templateVariableList.add(matcher.group());
        }
        return templateVariableList;
    }

    /**
     * 解析模板内容
     * @param templateContent
     * @param variableData
     * @return
     * @throws Exception
     */
    public static String parseTemplateContent(String templateContent, Object variableData) throws Exception {
        // 提取模版中使用到的模版变量
        List<String> messageTemplateVariables = getMessageTemplateVariables(templateContent);
        if(V.isEmpty(messageTemplateVariables)){
            return templateContent;
        }
        // 从缓存中获取变量对应的方法
        List<String> variableValueList = new ArrayList<>();
        List<Field> fields = BeanUtils.extractFields(variableData.getClass(), BindVariable.class);
        if (V.isEmpty(fields)) {
            log.warn("{} 类中无@BindVariable变量绑定注解，无法替换变量", variableData.getClass());
            return templateContent;
        }
        for (String variable : messageTemplateVariables) {
            // 执行方法，获取变量值
            for(Field field : fields){
                BindVariable bindVariable = field.getAnnotation(BindVariable.class);
                String fileName = "${" + field.getName() + "}";
                if(variable.equals(bindVariable.name()) || variable.equals(fileName)) {
                    Object value = BeanUtils.getProperty(variableData, field.getName());
                    if (value != null) {
                        variableValueList.add(String.valueOf(value));
                    } else {
                        log.warn("【执行方法获取内容为空】：当前变量为：{}", variable);
                        variableValueList.add("");
                    }
                    break;
                }
            }
        }
        return S.replaceEach(templateContent, S.toStringArray(messageTemplateVariables), S.toStringArray(variableValueList));
    }

}
