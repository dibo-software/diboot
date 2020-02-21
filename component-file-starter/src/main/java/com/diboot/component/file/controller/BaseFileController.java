package com.diboot.component.file.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.diboot.component.file.entity.UploadFile;
import com.diboot.component.file.service.UploadFileService;
import com.diboot.component.file.util.FileHelper;
import com.diboot.core.controller.BaseController;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
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
        return new JsonResult(Status.OK, entityList).bindPagination(pagination);
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
        // 文件后缀
        String ext = FileHelper.getFileExtByName(originFileName);
        // 先保存文件
        String fileUid = S.newUuid();
        String newFileName = fileUid + "." + ext;
        String fullPath = FileHelper.saveFile(file, newFileName);
        String description = getString(request, "description");
        // 保存文件上传记录
        createUploadFile(entityClass, fileUid, originFileName, fullPath, ext, description);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("uuid", fileUid);
        return new JsonResult(Status.OK).data(dataMap);
    }

    /**
     * 保存上传文件信息
     * @param entityClass
     * @param fileUid
     * @param originFileName
     * @param storagePath
     * @param fileType
     * @param description
     * @param <T>
     * @throws Exception
     */
    protected <T> void createUploadFile(Class<T> entityClass, String fileUid, String originFileName, String storagePath, String fileType, String description) throws Exception{
        // 保存文件之后的处理逻辑
        int dataCount = extractDataCount(fileUid, storagePath);
        UploadFile uploadFile = new UploadFile();
        uploadFile.setUuid(fileUid).setFileName(originFileName).setFileType(fileType);
        uploadFile.setRelObjType(entityClass.getSimpleName()).setStoragePath(storagePath);
        // 初始设置为0，批量保存数据后更新
        uploadFile.setDataCount(dataCount).setDescription(description);
        // 保存文件上传记录
        uploadFileService.createEntity(uploadFile);
    }

    /**
     * 保存文件之后的处理逻辑，如解析excel
     */
    protected int extractDataCount(String fileUuid, String fullPath) throws Exception{
        return 0;
    }

}
