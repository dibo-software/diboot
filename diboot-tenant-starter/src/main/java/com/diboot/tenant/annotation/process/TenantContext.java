package com.diboot.tenant.annotation.process;

import com.diboot.core.util.V;


/**
 * 租户上下文
 *
 * @author : uu
 * @version : v3.2.0
 * @Date 2023/12/18
 */
public class TenantContext implements Context<String> {
    /**
     * 存放当次请求的租户id
     */
    private final ThreadLocal<String> TENANT_ID = new InheritableThreadLocal();

    @Override
    public String get() {
        String tenantId = TENANT_ID.get();
        if (V.isEmpty(tenantId)) {
            tenantId = "0";
        }
        return V.isEmpty(tenantId) ? "0" : tenantId;
    }

    @Override
    public void set(String context) {
        TENANT_ID.set(context);
    }

    @Override
    public void remove() {
        TENANT_ID.remove();
    }
}
