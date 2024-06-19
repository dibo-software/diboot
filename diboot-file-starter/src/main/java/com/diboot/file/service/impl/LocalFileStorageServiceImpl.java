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
import com.diboot.file.entity.FileRecord;
import com.diboot.file.service.FileStorageService;
import com.diboot.file.util.FileHelper;
import com.diboot.file.util.HttpHelper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 本地存储
 *
 * @author : uu
 * @version : v2.0
 * @Date 2021/1/11  11:58
 */
@Slf4j
public class LocalFileStorageServiceImpl implements FileStorageService {

    @Override
    public FileRecord save(MultipartFile file) throws Exception {
        String fileUid = S.newUuid();
        String ext = FileHelper.getFileExtByName(file.getOriginalFilename());
        String newFileName = V.notEmpty(ext)? fileUid + "." + ext : fileUid;
        String fileFullPath = FileHelper.saveFile(file, newFileName);
       return new FileRecord(fileUid)
               .setFileName(file.getOriginalFilename())
               .setFileType(ext)
               .setFileSize(file.getSize())
               .setStoragePath(fileFullPath)
               .setAccessUrl(buildAccessUrl(fileUid,ext));
    }

    @Override
    public FileRecord save(InputStream inputStream, String fileName, long size) throws Exception {
        // 文件后缀
        String fileUid = S.newUuid();
        String ext = FileHelper.getFileExtByName(fileName);
        String newFileName = V.notEmpty(ext)? fileUid + "." + ext : fileUid;
        String fileFullPath = FileHelper.saveFile(inputStream, newFileName);
        return new FileRecord(fileUid)
                .setFileName(fileName)
                .setFileType(ext)
                .setFileSize(size)
                .setStoragePath(fileFullPath)
                .setAccessUrl(buildAccessUrl(fileUid,ext));
    }

    @Override
    public FileRecord save(String diskFilePath, String fileName) throws Exception {
        File file = new File(diskFilePath);
        if(!file.exists()) {
            log.error("文件: {} 不存在！", diskFilePath);
            throw new BusinessException("exception.business.file.nonexist");
        }
        // 文件后缀
        String fileUid = S.newUuid();
        String ext = FileHelper.getFileExtByName(fileName);
        return new FileRecord(fileUid)
                .setFileName(fileName)
                .setFileType(ext)
                .setFileSize(file.length())
                .setStoragePath(diskFilePath)
                .setAccessUrl(buildAccessUrl(fileUid, ext));
    }

    @Override
    public InputStream getFile(String filePath) throws Exception {
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            return Files.newInputStream(Paths.get(filePath));
        }
        return null;
    }

    @Override
    public void download(FileRecord uploadFile, HttpServletResponse response) throws Exception {
        if (V.isEmpty(uploadFile)) {
            throw new BusinessException(Status.FAIL_OPERATION, "exception.business.fileStorageService.nonexist");
        }
        HttpHelper.downloadLocalFile(uploadFile.getStoragePath(), uploadFile.getFileName(), response);
    }

    @Override
    public boolean delete(String filePath) {
        File file = new File(filePath);
        return file.delete();
    }

}
