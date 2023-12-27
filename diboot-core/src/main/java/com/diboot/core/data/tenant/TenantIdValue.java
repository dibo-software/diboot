package com.diboot.core.data.tenant;

import com.diboot.core.config.BaseConfig;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.ContextHolder;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;

import java.util.List;

/**
 * 租户id值
 *
 * @author : uu
 * @version : v3.2
 * @Date 2023/12/27
 */
public interface TenantIdValue<T> {

    /**
     * 获取当前登录用户租户id
     * *
     *
     * @return
     */
    T get();

    /**
     * 获取当前登录用户租户id 和 平台租户id
     *
     * @return
     */
    List<T> gets();

    /**
     * 检查是否启用
     * @return
     */
    static boolean validEnabled() {
        if (BaseConfig.isEnableTenant() && V.isEmpty(ContextHolder.getBean(TenantIdValue.class))) {
            throw new BusinessException(Status.FAIL_SERVICE_UNAVAILABLE, "租户未启用相关实现");
        }
        return BaseConfig.isEnableTenant();
    }
}
