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

import com.diboot.file.dto.UploadFileResult;
import com.diboot.file.entity.UploadFile;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 文件操作接口
 *
 * @author : uu
 * @version : v2.0
 * @Date 2021/1/11  11:42
 */
public interface FileStorageService {

    /**
     * 上传文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    UploadFileResult upload(MultipartFile file) throws Exception;

    /**
     * 获取文件
     *
     * @param uploadFile
     * @param response
     * @return
     * @throws Exception
     */
    void download(UploadFile uploadFile, HttpServletResponse response) throws Exception;

}
