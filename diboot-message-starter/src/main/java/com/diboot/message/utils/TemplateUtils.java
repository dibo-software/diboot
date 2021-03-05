package com.diboot.message.utils;

import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.V;
import com.diboot.message.annotation.BindVariable;
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
 */
public class TemplateUtils {

    public static List<String> TEMPLATE_STRATEGY_LIST = new ArrayList<>();

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
            BindVariable bindVariable = method.getAnnotation(BindVariable.class);
            if (bindVariable == null) {
                continue;
            }
            TEMPLATE_STRATEGY_LIST.add(bindVariable.name());
            TemplateVariableService.TEMPLATE_STRATEGY_CACHE.put(bindVariable.name(), method);
        }
        return TEMPLATE_STRATEGY_LIST;
    }
}
