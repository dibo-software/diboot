/*
 * Copyright (c) 2015-2021, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.file.service;

import com.diboot.core.service.BaseService;
import com.diboot.file.entity.UploadFile;

import java.util.List;

/**
 * 基础文件Service
 *
 * @author Lishuaifei@dibo.ltd
 * @date 2019-07-18
 */
public interface UploadFileService extends BaseService<UploadFile> {

    /**
     * 获取指定对象记录关联的上传文件列表
     *
     * @param relObjClass
     * @param relObjId
     * @return
     */
    List<UploadFile> getUploadedFiles(String relObjClass, Object relObjId);

    /**
     * 绑定业务id
     *
     * @param relObjId
     * @param relObjTypeClass
     * @param fileUuidList
     * @throws Exception
     */
    void bindRelObjId(Object relObjId, Class<?> relObjTypeClass, List<String> fileUuidList) throws Exception;

}