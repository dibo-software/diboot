package com.diboot.iam.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.task.TaskDecorator;

/**
 * Shiro 上下文主子线程用户信息传递
 * @author JerryMa
 * @version v3.5.0
 * @date 2024/7/12
 */
public class ShiroContextTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        try {
            LocaleContextHolder.setLocale(LocaleContextHolder.getLocale(), true);
            // 向下传递当前线程的用户信息
            return SecurityUtils.getSubject().associateWith(runnable);
        }
        catch (UnavailableSecurityManagerException e) {
            // 用户信息不存在，直接执行
            return runnable;
        }
    }

}
