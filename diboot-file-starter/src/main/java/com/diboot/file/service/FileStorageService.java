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

import com.diboot.core.exception.InvalidUsageException;
import com.diboot.core.util.V;
import com.diboot.file.entity.FileRecord;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 文件操作接口
 *
 * @author : uu
 * @version : v2.0
 * @Date 2021/1/11  11:42
 */
public interface FileStorageService {

    /**
     * 构建访问地址
     *
     * @param fileUid 文件UUID
     * @param ext     文件扩展名
     * @return 访问地址
     */
    default String buildAccessUrl(String fileUid, String ext) {
        return "/file/" + fileUid + (V.notEmpty(ext)? "." + ext : "");
    }

    /**
     * 上传文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    FileRecord save(MultipartFile file) throws Exception;

    /**
     * 上传文件
     *
     * @param inputStream
     * @param fileName
     * @param size
     * @return
     * @throws Exception
     */
    FileRecord save(InputStream inputStream, String fileName, long size) throws Exception;

    /**
     * 上传文件
     *
     * @param diskFilePath 磁盘中已存在的临时文件
     * @param fileName 文件名
     * @return
     * @throws Exception
     */
    default FileRecord save(String diskFilePath, String fileName) throws Exception {
        throw new InvalidUsageException("exception.invalidUsage.fileStorageService.save.message");
    }

    /**
     * 获取文件
     *
     * @param filePath 文件路径
     * @return
     * @throws Exception
     */
    InputStream getFile(String filePath) throws Exception;

    /**
     * 获取文件
     *
     * @param uploadFile
     * @param response
     * @return
     * @throws Exception
     */
    void download(FileRecord uploadFile, HttpServletResponse response) throws Exception;

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    boolean delete(String filePath);
}
