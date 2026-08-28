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
import com.diboot.core.exception.InvalidUsageException;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.V;
import com.diboot.file.entity.FileRecord;
import com.diboot.file.mapper.FileRecordMapper;
import com.diboot.file.service.FileRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

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

}
