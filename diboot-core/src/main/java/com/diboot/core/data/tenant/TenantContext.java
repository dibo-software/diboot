package com.diboot.core.data.tenant;

import com.diboot.core.config.BaseConfig;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.ContextHolder;
import com.diboot.core.util.PropertiesUtils;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * 租户id值
 *
 * @author : uu
 * @version : v3.2
 * @Date 2023/12/27
 */
public interface TenantContext<T> {

    /**
     * 获取当前登录用户租户id
     * *
     *
     * @return
     */
    T getTenantId();

    /**
     * 获取当前登录用户租户id 和 平台租户id
     *
     * @return
     */
    List<T> getTenantIds();

    /**
     * 检查是否开启
     *
     * @return
     */
    static boolean isEnabled() {
        return BaseConfig.isEnableTenant();
    }

    /**
     * 检查是否开启
     * @param environment 预先填充 environment 避免从environment中获取配置值异常
     * @return
     */
    static boolean isEnabled(Environment environment) {
        PropertiesUtils.setEnvironment(environment);
        return BaseConfig.isEnableTenant();
    }

    /**
     * 获取当前登录用户租户id
     *
     * @return
     */
    static <R> R get() {
        TenantContext<R> context = ContextHolder.getBean(TenantContext.class);
        if (V.isEmpty(context)) {
            throw new BusinessException(Status.FAIL_SERVICE_UNAVAILABLE, "租户未启用相关实现");
        }
        return context.getTenantId();
    }

    /**
     * 获取当前登录用户租户id
     * <p>
     * 如果没有（值不存在，或者没有实现上下文，返回0）
     *
     * @return
     */
    static <R> R getOrDefault(R defaultVal) {
        TenantContext<R> tenantContext = ContextHolder.getBean(TenantContext.class);
        return V.isEmpty(tenantContext) || V.isEmpty(tenantContext.getTenantId())
                ? defaultVal : tenantContext.getTenantId();
    }

    /**
     * 获取当前登录用户租户id 和 平台租户id
     *
     * @return
     */
    static <R> List<R> gets() {
        TenantContext<R> context = ContextHolder.getBean(TenantContext.class);
        if (V.isEmpty(context)) {
            throw new BusinessException(Status.FAIL_SERVICE_UNAVAILABLE, "租户未启用相关实现");
        }
        return context.getTenantIds();
    }
}
