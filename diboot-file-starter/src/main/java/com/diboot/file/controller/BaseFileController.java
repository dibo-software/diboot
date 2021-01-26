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
package com.diboot.file.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.controller.BaseController;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import com.diboot.file.dto.UploadFileFormDTO;
import com.diboot.file.dto.UploadFileResult;
import com.diboot.file.entity.UploadFile;
import com.diboot.file.service.FileStorageService;
import com.diboot.file.service.UploadFileService;
import com.diboot.file.util.FileHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

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

    @Autowired
    protected FileStorageService fileStorageService;

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
     * @param
     * @return
     * @throws Exception
     */
    public <T> JsonResult uploadFile(MultipartFile file, Class<T> entityClass) throws Exception {
        if(file == null) {
            throw new BusinessException(Status.FAIL_INVALID_PARAM, "未获取待处理的文件！");
        }
        String originFileName = file.getOriginalFilename();
        if (V.isEmpty(originFileName) || !FileHelper.isValidFileExt(originFileName)) {
            log.debug("非法的文件类型: " + originFileName);
            throw new BusinessException(Status.FAIL_VALIDATION, "请上传合法的文件格式！");
        }
        // 保存文件
        UploadFile uploadFile = saveFile(file, entityClass);
        // 保存上传记录
        createUploadFile(uploadFile);
        // 返回结果
        // 返回结果
        return JsonResult.OK(new HashMap(16) {{
            put("uuid", uploadFile.getUuid());
            put("accessUrl", uploadFile.getAccessUrl());
            put("fileName", uploadFile.getFileName());
        }});
    }

    /**
     * 保存文件
     * @param file
     * @param entityClass
     * @param <T>
     * @return
     * @throws Exception
     */
    protected <T> UploadFile saveFile(MultipartFile file, Class<T> entityClass) throws Exception{
        UploadFileResult uploadFileResult = fileStorageService.upload(file);
        UploadFile uploadFile = new UploadFile();
        uploadFile.setUuid(uploadFileResult.getUuid())
                .setFileName(uploadFileResult.getOriginalFilename())
                .setFileType(uploadFileResult.getExt())
                .setRelObjType(entityClass.getSimpleName())
                .setStoragePath(uploadFileResult.getStorageFullPath());
        String accessUrl = buildAccessUrl(uploadFileResult.getFilename());
        uploadFile.setAccessUrl(accessUrl);
        String description = getString("description");
        uploadFile.setDescription(description);
        // 返回uploadFile对象
        return uploadFile;
    }

    /***
     * 直接上传文件
     * @param uploadFileFormDTO
     * @return
     * @throws Exception
     */
    public JsonResult uploadFile(UploadFileFormDTO uploadFileFormDTO) throws Exception {
        if(uploadFileFormDTO == null || uploadFileFormDTO.getFile() == null) {
            throw new BusinessException(Status.FAIL_INVALID_PARAM, "未获取待处理的文件！");
        }
        String originFileName = uploadFileFormDTO.getFile().getOriginalFilename();
        if (V.isEmpty(originFileName) || !FileHelper.isValidFileExt(originFileName)) {
            log.debug("非法的文件类型: " + originFileName);
            throw new BusinessException(Status.FAIL_VALIDATION, "请上传合法的文件格式！");
        }
        // 保存文件
        UploadFile uploadFile = saveFile(uploadFileFormDTO);
        // 保存上传记录
        createUploadFile(uploadFile);
        // 返回结果
        return JsonResult.OK(new HashMap(16) {{
            put("uuid", uploadFile.getUuid());
            put("accessUrl", uploadFile.getAccessUrl());
            put("fileName", uploadFile.getFileName());
        }});
    }

    /**
     * 保存文件
     * @param uploadFileFormDTO
     * @return
     * @throws Exception
     */
    protected UploadFile saveFile(UploadFileFormDTO uploadFileFormDTO) throws Exception{
        UploadFileResult uploadFileResult= fileStorageService.upload(uploadFileFormDTO.getFile());
        String accessUrl = buildAccessUrl(uploadFileResult.getFilename());
        UploadFile uploadFile = new UploadFile();
        uploadFile.setUuid(uploadFileResult.getUuid())
                .setFileName(uploadFileResult.getOriginalFilename())
                .setFileType(uploadFileResult.getExt())
                .setRelObjType(uploadFileFormDTO.getRelObjType())
                .setRelObjField(uploadFileFormDTO.getRelObjField())
                .setStoragePath(uploadFileResult.getStorageFullPath())
                .setAccessUrl(accessUrl)
                .setDescription(uploadFileFormDTO.getDescription());
        // 返回uploadFile对象
        return uploadFile;
    }

    /**
     * 构建文件访问/下载的url
     * @param newFileName
     * @return
     */
    protected String buildAccessUrl(String newFileName){
        return "/uploadFile/download/" + newFileName;
    }

    /**
     * 保存上传文件信息
     * @param uploadFile
     * @throws Exception
     */
    protected void createUploadFile(UploadFile uploadFile) throws Exception{
        // 保存文件之后的处理逻辑
        int dataCount = extractDataCount(uploadFile.getUuid(), uploadFile.getStoragePath());
        uploadFile.setDataCount(dataCount);
        // 保存文件上传记录
        uploadFileService.createEntity(uploadFile);
    }

    /**
     * <h3>获取文件通用接口</h3>
     * <p>
     * 其中当relObjField不传递的时候，表示获取当前业务ID和业务类型下的所有文件<br/>
     * 当传递relObjField的时候，获取指定类型的文件
     * </p>
     *
     * @param relObjId   业务ID   <strong style="color:red;">必传字段</strong>
     * @param relObjType 业务类型 <strong style="color:red;">必传字段</strong>
     * @param relObjField 对应的具体类型   <strong style="color:blue;">非必传字段(同一种业务下可能有多种文件)</strong>
     * @return {@link List <UploadFile>} 返回文件对象的集合
     * @throws Exception
     */
    public List<UploadFile> getUploadFileList(Object relObjId, String relObjType, String relObjField) throws Exception {
        LambdaQueryWrapper<UploadFile> wrapper = Wrappers.<UploadFile>lambdaQuery()
                .eq(UploadFile::getRelObjId, relObjId)
                .eq(UploadFile::getRelObjType, relObjType);
        if (V.notEmpty(relObjField)) {
            wrapper.eq(UploadFile::getRelObjField, relObjField);
        }
        return uploadFileService.getEntityList(wrapper);
    }

    /**
     * 保存文件之后的处理逻辑，如解析excel
     */
    protected int extractDataCount(String fileUuid, String fullPath) throws Exception{
        return 0;
    }

}
