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
package com.diboot.file.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.exception.InvalidUsageException;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Status;
import com.diboot.file.entity.FileRecord;
import com.diboot.file.mapper.FileRecordMapper;
import com.diboot.file.service.FileAccessAuthorizer;
import com.diboot.file.service.FileRecordService;
import com.diboot.iam.annotation.OperationCons;
import com.diboot.iam.exception.PermissionException;
import com.diboot.iam.util.IamSecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 文件记录 Service 实现
 *
 * @author wind
 * @version v3.0.0
 * @date 2022-05-30
 */
@Slf4j
@Service
public class FileRecordServiceImpl extends BaseServiceImpl<FileRecordMapper, FileRecord> implements FileRecordService {

    private static final String UNKNOWN_BUSINESS_TYPE = "UNKNOWN";

    @Autowired(required = false)
    private List<FileAccessAuthorizer> fileAccessAuthorizers = Collections.emptyList();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBusinessId(String businessId, Collection<String> fileIds) {
        if (V.isEmpty(businessId)) {
            throw new InvalidUsageException("businessId不能为空");
        }
        // 移除旧数据
        remove(Wrappers.<FileRecord>lambdaQuery()
            .eq(FileRecord::getBusinessId, businessId)
            .notIn(V.notEmpty(fileIds), FileRecord::getId, fileIds));
        // 添加新数据
        if (V.notEmpty(fileIds)) {
            update(Wrappers.<FileRecord>lambdaUpdate()
                .set(FileRecord::getBusinessId, businessId)
                .in(FileRecord::getId, fileIds));
        }
    }

    @Override
    public void checkFileWritePermission(String businessType) {

        // 验证业务类型
        if (V.isEmpty(businessType)) {
            throw new BusinessException(Status.FAIL_INVALID_PARAM, "businessType不能为空，请指定业务类型");
        }
        if (!IamSecurityUtils.isSuperAdmin()) {
            try {
                IamSecurityUtils.getSubject().checkPermission(businessType + ":" + OperationCons.CODE_WRITE);
            } catch (Exception e) {
                log.warn("文件上传权限验证失败:缺少权限{}， {}", businessType + ":" + OperationCons.CODE_WRITE ,e.getMessage());
                throw new BusinessException(Status.FAIL_NO_PERMISSION, "没有权限上传文件！");
            }
        }
    }

    @Override
    public void checkFileReadPermission(FileRecord fileRecord) {
        if (!V.equals(fileRecord.getTenantId(), IamSecurityUtils.getCurrentTenantId())) {
            throw new PermissionException();
        }
        String businessType = fileRecord.getBusinessType();
        if (UNKNOWN_BUSINESS_TYPE.equalsIgnoreCase(businessType)) {
            return;
        }
        if (V.isEmpty(businessType)) {
            log.warn("文件{}缺少业务类型", fileRecord.getId());
            throw new PermissionException("文件缺失业务类型");
        }
        // 管理员直接放权
        if (IamSecurityUtils.isSuperAdmin()) {
            return;
        }
        try {
            IamSecurityUtils.getSubject().checkPermission(businessType + ":" + OperationCons.CODE_READ);
        } catch (Exception e) {
            log.warn("文件{}读权限", fileRecord.getId(), e);
            throw new PermissionException("文件无读权限");
        }
        for (FileAccessAuthorizer authorizer : fileAccessAuthorizers) {
            if (V.notEquals(businessType, authorizer.getBusinessType())) {
                continue;
            }
            if (V.isEmpty(fileRecord.getBusinessId()) || !authorizer.canRead(fileRecord)) {
                throw new PermissionException();
            }
        }
    }

}
