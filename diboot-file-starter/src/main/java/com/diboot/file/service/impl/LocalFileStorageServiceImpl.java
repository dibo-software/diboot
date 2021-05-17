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
package com.diboot.file.service.impl;

import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.file.dto.UploadFileResult;
import com.diboot.file.entity.UploadFile;
import com.diboot.file.service.FileStorageService;
import com.diboot.file.util.FileHelper;
import com.diboot.file.util.HttpHelper;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * 本地存储
 *
 * @author : uu
 * @version : v2.0
 * @Date 2021/1/11  11:58
 */
public class LocalFileStorageServiceImpl implements FileStorageService {

    @Override
    public UploadFileResult upload(MultipartFile file) throws Exception {
        UploadFileResult uploadFileResult = new UploadFileResult();
        // 文件后缀
        String fileUid = S.newUuid();
        String ext = FileHelper.getFileExtByName(file.getOriginalFilename());
        String newFileName = fileUid + "." + ext;
        String fileFullPath = FileHelper.saveFile(file, newFileName);
        uploadFileResult.setOriginalFilename(file.getOriginalFilename())
                .setExt(ext)
                .setUuid(fileUid)
                .setFilename(newFileName)
                .setStorageFullPath(fileFullPath);
        return uploadFileResult;
    }

    @Override
    public UploadFileResult upload(InputStream inputStream, String fileName) throws Exception {
        UploadFileResult uploadFileResult = new UploadFileResult();
        // 文件后缀
        String fileUid = S.newUuid();
        String ext = FileHelper.getFileExtByName(fileName);
        String newFileName = fileUid + "." + ext;
        String fileFullPath = FileHelper.saveFile(inputStream, newFileName);
        uploadFileResult.setOriginalFilename(fileName)
                .setExt(ext)
                .setUuid(fileUid)
                .setFilename(newFileName)
                .setStorageFullPath(fileFullPath);
        return uploadFileResult;
    }

    @Override
    public void download(UploadFile uploadFile, HttpServletResponse response) throws Exception {
        if (V.isEmpty(uploadFile)) {
            throw new BusinessException(Status.FAIL_OPERATION, "文件不存在");
        }
        HttpHelper.downloadLocalFile(uploadFile.getStoragePath(), uploadFile.getFileName(), response);
    }
}
