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
