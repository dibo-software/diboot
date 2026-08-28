/*
 * Copyright (c) 2015-2099, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.file.interceptor;

import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.file.config.FileProperties;
import com.diboot.file.entity.FileRecord;
import com.diboot.file.service.FileAccessAuthorizer;
import com.diboot.iam.annotation.OperationCons;
import com.diboot.iam.exception.PermissionException;
import com.diboot.iam.util.IamSecurityUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

/**
 * 文件访问拦截器默认实现。
 * <p>
 * 读取检查顺序：租户隔离 -> 超管放行 -> 遗留数据策略 -> 功能权限({businessType}:read) -> 对象级授权({@link FileAccessAuthorizer})。
 * <p>
 * 写入检查顺序：业务类型必填 -> 超管放行 -> 功能权限({businessType}:write)。
 *
 * @author diboot
 * @version v3.9.1
 */
@Slf4j
public class DefaultFileAccessInterceptor implements FileAccessInterceptor {

    /**
     * 遗留数据的业务类型标识
     */
    protected static final String UNKNOWN_BUSINESS_TYPE = "UNKNOWN";

    private final FileProperties fileProperties;

    private final List<FileAccessAuthorizer> fileAccessAuthorizers;

    public DefaultFileAccessInterceptor(FileProperties fileProperties, List<FileAccessAuthorizer> fileAccessAuthorizers) {
        this.fileProperties = fileProperties;
        this.fileAccessAuthorizers = V.notEmpty(fileAccessAuthorizers) ? fileAccessAuthorizers : Collections.emptyList();
    }

    @Override
    public void checkRead(FileRecord fileRecord) {
        // 租户隔离校验（安全底线，不可绕过）
        if (!V.equals(fileRecord.getTenantId(), IamSecurityUtils.getCurrentTenantId())) {
            throw new PermissionException();
        }
        if (IamSecurityUtils.isSuperAdmin()) {
            return;
        }
        if (isLegacyFile(fileRecord)) {
            checkLegacyFile(fileRecord);
            return;
        }
        String businessType = fileRecord.getBusinessType();
        checkFunctionalPermission(businessType, OperationCons.CODE_READ);
        checkObjectReadPermission(fileRecord, businessType);
    }

    @Override
    public void checkWrite(String businessType) {
        if (V.isEmpty(businessType)) {
            throw new BusinessException(Status.FAIL_INVALID_PARAM, "businessType不能为空，请指定业务类型");
        }
        if (IamSecurityUtils.isSuperAdmin()) {
            return;
        }
        checkFunctionalPermission(businessType, OperationCons.CODE_WRITE);
    }

    /**
     * 是否为遗留数据（无业务类型或标记为 UNKNOWN）
     */
    protected boolean isLegacyFile(FileRecord fileRecord) {
        String businessType = fileRecord.getBusinessType();
        return V.isEmpty(businessType) || UNKNOWN_BUSINESS_TYPE.equalsIgnoreCase(businessType);
    }

    /**
     * 遗留数据的访问检查，按配置的 {@link FileProperties.Access.LegacyPolicy} 处理
     */
    protected void checkLegacyFile(FileRecord fileRecord) {
        FileProperties.Access.LegacyPolicy policy = fileProperties.getAccess().getLegacyPolicy();
        switch (policy) {
            case ALLOW:
                return;
            case OWNER_ONLY:
                if (V.notEquals(fileRecord.getCreateBy(), IamSecurityUtils.getCurrentUserId())) {
                    log.warn("遗留文件{}仅限上传者访问, policy={}", fileRecord.getId(), policy);
                    throw new PermissionException();
                }
                return;
            case DENY:
            default:
                log.warn("遗留文件{}禁止访问, policy={}", fileRecord.getId(), policy);
                throw new PermissionException();
        }
    }

    /**
     * 功能权限检查：{businessType}:{permissionCode}
     * @param businessType
     * @param permissionCode
     */
    protected void checkFunctionalPermission(String businessType, String permissionCode) {
        try {
            IamSecurityUtils.getSubject().checkPermission(businessType + ":" + permissionCode);
        } catch (Exception e) {
            log.warn("文件权限校验失败: 缺少权限 {}", businessType + ":" + permissionCode);
            throw new PermissionException();
        }
    }

    /**
     * 对象级授权检查：委派给业务类型匹配的 {@link FileAccessAuthorizer}
     */
    protected void checkObjectReadPermission(FileRecord fileRecord, String businessType) {
        for (FileAccessAuthorizer authorizer : fileAccessAuthorizers) {
            if (V.notEquals(businessType, authorizer.getBusinessType())) {
                continue;
            }
            if (!authorizer.canRead(fileRecord)) {
                throw new PermissionException();
            }
            return;
        }
        log.debug("业务类型{}未注册 FileAccessAuthorizer，仅校验功能权限", businessType);
    }

}
