/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.V;
import com.diboot.file.entity.UploadFile;
import com.diboot.file.mapper.UploadFileMapper;
import com.diboot.file.service.UploadFileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 文件下载实现类
 * @author Lishuaifei@dibo.ltd
 * @date 2019-07-18
 */
@Service
public class UploadFileServiceImpl extends BaseServiceImpl<UploadFileMapper, UploadFile> implements UploadFileService {

    @Override
    public List<UploadFile> getUploadedFiles(String relObjClass, Object relObjId) {
        LambdaQueryWrapper<UploadFile> queryWrapper = new QueryWrapper<UploadFile>().lambda()
                .eq(UploadFile::getRelObjType, relObjClass)
                .eq(UploadFile::getRelObjId, relObjId.toString())
                .orderByAsc(UploadFile::getCreateTime);
        return getEntityList(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindRelObjId(Object relObjId, Class<?> relObjTypeClass, List<String> fileUuidList) {
        // 如果fileUuidList为null，不进行绑定关联更新
        if (fileUuidList == null) {
            return;
        }
        // 删除 relObjId + relObjType下的 并且不包含fileIdList的file。
        this.update(
                Wrappers.<UploadFile>lambdaUpdate()
                        .set(UploadFile::isDeleted, true)
                        .eq(UploadFile::getRelObjId, relObjId.toString())
                        .eq(UploadFile::getRelObjType, relObjTypeClass.getSimpleName())
                        .notIn(V.notEmpty(fileUuidList), UploadFile::getUuid, fileUuidList)
        );
        // 绑定绑定数据
        if (V.notEmpty(fileUuidList)) {
            this.update(
                    Wrappers.<UploadFile>lambdaUpdate()
                            .set(UploadFile::getRelObjId, relObjId.toString())
                            .in(UploadFile::getUuid, fileUuidList)
            );
        }
    }
}
