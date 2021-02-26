package com.diboot.message.service;

import com.diboot.core.util.V;
import com.diboot.message.annotation.BindVariable;
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
 */
public interface TemplateVariableService {

    /**
     * 非贪婪 解析正则 ${}
     */
    Pattern TEMPLATE_VARIABLE = Pattern.compile("\\$\\{.+?\\}");

    /**
     * 变量方法缓存<{@link BindVariable#name()}, Method>
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
            TemplateUtils.loadTemplateTemplateVariableList();
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
        return templateVariableList.toArray(new String[templateVariableList.size()]);
    }

}
