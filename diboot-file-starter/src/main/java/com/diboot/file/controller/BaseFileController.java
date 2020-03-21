package com.diboot.file.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.diboot.core.controller.BaseController;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import com.diboot.file.entity.UploadFile;
import com.diboot.file.service.UploadFileService;
import com.diboot.file.util.FileHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel导入基类Controller
 * @author Mazc@dibo.ltd
 * @version 2.0
 * @date 2020/02/20
 */
@Slf4j
public abstract class BaseFileController extends BaseController {
    @Autowired
    protected UploadFileService uploadFileService;

    /***
     * 获取文件上传记录
     * <p>
     * url参数示例: /${bindURL}?pageSize=20&pageIndex=1
     * </p>
     * @return JsonResult
     * @throws Exception
     */
    protected JsonResult getEntityListWithPaging(Wrapper queryWrapper, Pagination pagination) throws Exception {
        // 查询当前页的数据
        List entityList = uploadFileService.getEntityList(queryWrapper, pagination);
        // 返回结果
        return JsonResult.OK(entityList).bindPagination(pagination);
    }

    /***
     * 直接上传文件
     * @param request
     * @param
     * @return
     * @throws Exception
     */
    public <T> JsonResult uploadFile(MultipartFile file, Class<T> entityClass, HttpServletRequest request) throws Exception {
        if(file == null) {
            throw new BusinessException(Status.FAIL_INVALID_PARAM, "未获取待处理的文件！");
        }
        String originFileName = file.getOriginalFilename();
        if (V.isEmpty(originFileName) || !FileHelper.isValidFileExt(originFileName)) {
            log.debug("非法的文件类型: " + originFileName);
            throw new BusinessException(Status.FAIL_VALIDATION, "请上传合法的文件格式！");
        }
        // 保存文件
        UploadFile uploadFile = saveFile(file, entityClass, request);
        // 保存上传记录
        createUploadFile(uploadFile, request);
        // 返回结果
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("uuid", uploadFile.getUuid());
        dataMap.put("accessUrl", uploadFile.getAccessUrl());
        return JsonResult.OK(dataMap);
    }

    /**
     * 保存文件
     * @param file
     * @param entityClass
     * @param request
     * @param <T>
     * @return
     * @throws Exception
     */
    protected <T> UploadFile saveFile(MultipartFile file, Class<T> entityClass, HttpServletRequest request) throws Exception{
        // 文件后缀
        String originFileName = file.getOriginalFilename();
        String ext = FileHelper.getFileExtByName(file.getOriginalFilename());
        // 先保存文件
        String fileUid = S.newUuid();
        String newFileName = fileUid + "." + ext;
        String storageFullPath = FileHelper.saveFile(file, newFileName);

        UploadFile uploadFile = new UploadFile();
        uploadFile.setUuid(fileUid).setFileName(originFileName).setFileType(ext);
        uploadFile.setRelObjType(entityClass.getSimpleName()).setStoragePath(storageFullPath);

        String description = getString(request, "description");
        uploadFile.setDescription(description);
        // 返回uploadFile对象
        return uploadFile;
    }

    /**
     * 保存上传文件信息
     * @param uploadFile
     * @param request
     * @throws Exception
     */
    protected void createUploadFile(UploadFile uploadFile, HttpServletRequest request) throws Exception{
        // 保存文件之后的处理逻辑
        int dataCount = extractDataCount(uploadFile.getUuid(), uploadFile.getStoragePath(), request);
        uploadFile.setDataCount(dataCount);
        // 保存文件上传记录
        uploadFileService.createEntity(uploadFile);
    }

    /**
     * 保存文件之后的处理逻辑，如解析excel
     */
    protected int extractDataCount(String fileUuid, String fullPath, HttpServletRequest request) throws Exception{
        return 0;
    }

}
