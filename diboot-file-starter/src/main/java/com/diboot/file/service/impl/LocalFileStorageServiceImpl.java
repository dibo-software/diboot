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
        uploadFileResult.setOriginalFilename(file.getOriginalFilename())
                .setExt(ext)
                .setUuid(fileUid)
                .setFilename(newFileName)
                .setStorageFullPath(FileHelper.saveFile(file, newFileName));
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
