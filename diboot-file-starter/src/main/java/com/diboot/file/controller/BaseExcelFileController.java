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

import com.diboot.core.config.BaseConfig;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.exception.InvalidUsageException;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Status;
import com.diboot.file.dto.UploadFileFormDTO;
import com.diboot.file.entity.UploadFile;
import com.diboot.file.excel.listener.FixedHeadExcelListener;
import com.diboot.file.util.ExcelHelper;
import com.diboot.file.util.FileHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel导入基类Controller
 *
 * @author Mazc@dibo.ltd
 * @version 2.0
 * @date 2020/02/20
 */
@Slf4j
public abstract class BaseExcelFileController extends BaseFileController {
    // 初始文件名参数
    protected static final String ORIGIN_FILE_NAME = "originFileName";
    // 预览文件名参数
    protected static final String PREVIEW_FILE_NAME = "previewFileName";

    /***
     * 获取对应的ExcelDataListener
     * @return
     */
    protected abstract FixedHeadExcelListener getExcelDataListener();

    /***
     * excel数据预览
     * @return
     * @throws Exception
     */
    public JsonResult excelPreview(MultipartFile file) throws Exception {
        checkIsExcel(file);
        // 保存文件到本地
        UploadFile uploadFile = super.saveFile(file, getExcelDataListener().getExcelModelClass());
        // 构建预览数据Map
        Map<String, Object> dataMap = buildPreviewDataMap(uploadFile, file.getOriginalFilename());
        return JsonResult.OK(dataMap);
    }

    /***
     * excel数据预览
     * @param uploadFileFormDTO 带有其他配置的上传
     * @return
     * @throws Exception
     */
    public JsonResult excelPreview(UploadFileFormDTO uploadFileFormDTO) throws Exception {
        checkIsExcel(uploadFileFormDTO.getFile());
        // 保存文件到本地
        UploadFile uploadFile = super.saveFile(uploadFileFormDTO);
        // 构建预览数据Map
        Map<String, Object> dataMap = buildPreviewDataMap(uploadFile, uploadFileFormDTO.getFile().getOriginalFilename());
        return JsonResult.OK(dataMap);
    }

    /***
     * 预览后提交保存
     * @param
     * @return
     * @throws Exception
     */
    public <T> JsonResult excelPreviewSave(Class<T> entityClass, String previewFileName, String originFileName) throws Exception {
        if (V.isEmpty(previewFileName) || V.isEmpty(originFileName)) {
            throw new InvalidUsageException("预览保存失败，参数 tempFileName 或 originFileName 未指定！");
        }
        String fileUid = S.substringBefore(previewFileName, ".");
        String fullPath = FileHelper.getFullPath(previewFileName);
        String accessUrl = buildAccessUrl(previewFileName);
        String ext = FileHelper.getFileExtByName(originFileName);
        // 描述
        String description = getString("description");
        // 保存文件上传记录
        UploadFile uploadFile = new UploadFile().setUuid(fileUid)
                .setFileName(originFileName).setStoragePath(fullPath).setFileType(ext)
                .setAccessUrl(accessUrl)
                .setRelObjType(entityClass.getSimpleName()).setDescription(description);
        super.createUploadFile(uploadFile);
        return JsonResult.OK();
    }

    /***
     * 预览后提交保存
     * @param uploadFileFormDTO
     * @param previewFileName
     * @param originFileName
     * @return
     * @throws Exception
     */
    public <T> JsonResult excelPreviewSave(UploadFileFormDTO uploadFileFormDTO, String previewFileName, String originFileName) throws Exception {
        if (V.isEmpty(previewFileName) || V.isEmpty(originFileName)) {
            throw new InvalidUsageException("预览保存失败，参数 tempFileName 或 originFileName 未指定！");
        }
        String fileUid = S.substringBefore(previewFileName, ".");
        String fullPath = FileHelper.getFullPath(previewFileName);
        String accessUrl = buildAccessUrl(previewFileName);
        String ext = FileHelper.getFileExtByName(originFileName);
        // 保存文件上传记录
        UploadFile uploadFile = new UploadFile().setUuid(fileUid)
                .setFileName(originFileName).setStoragePath(fullPath).setFileType(ext)
                .setAccessUrl(accessUrl)
                .setRelObjField(uploadFileFormDTO.getRelObjField())
                .setRelObjType(uploadFileFormDTO.getRelObjType())
                .setDescription(uploadFileFormDTO.getDescription());
        super.createUploadFile(uploadFile);
        return JsonResult.OK();
    }

    /***
     * 直接上传excel
     * @param
     * @return
     * @throws Exception
     */
    public <T> JsonResult uploadExcelFile(MultipartFile file, Class<T> entityClass) throws Exception {
        checkIsExcel(file);
        return super.uploadFile(file, entityClass);
    }

    /***
     * 直接上传excel
     * @param
     * @return
     * @throws Exception
     */
    public <T> JsonResult uploadExcelFile(UploadFileFormDTO uploadFileFormDTO) throws Exception {
        checkIsExcel(uploadFileFormDTO.getFile());
        return super.uploadFile(uploadFileFormDTO);
    }

    /**
     * 构建预览数据Map
     * @param uploadFile
     * @param originFileName
     * @return
     * @throws Exception
     */
    private Map<String, Object> buildPreviewDataMap(UploadFile uploadFile, String originFileName) throws Exception{
        Map<String, Object> dataMap = new HashMap<>(8);
        // 预览
        FixedHeadExcelListener listener = getExcelDataListener();
        listener.setRequestParams(super.getParamsMap());
        // 读取excel
        readExcel(uploadFile, listener);

        //最后拦截，如果数据异常在listener中未被拦截抛出异常，此处进行处理
        if (V.notEmpty(listener.getErrorMsgs())) {
            throw new BusinessException(Status.FAIL_VALIDATION, S.join(listener.getErrorMsgs(), "; "));
        }
        // 绑定属性到model
        dataMap.put("header", listener.getFieldHeaders());
        dataMap.put(ORIGIN_FILE_NAME, originFileName);
        dataMap.put(PREVIEW_FILE_NAME, FileHelper.getFileName(uploadFile.getStoragePath()));
        List dataList = listener.getDataList();
        int totalCount = 0;
        if (V.notEmpty(dataList)) {
            totalCount = dataList.size();
            if(dataList.size() > BaseConfig.getPageSize()){
                dataList = dataList.subList(0, BaseConfig.getPageSize());
            }
        }
        //最多返回前端1页数据
        dataMap.put("dataList", dataList);
        dataMap.put("totalCount", totalCount);
        return dataMap;
    }

    /**
     * 读取excel方法，如果需要替换成远程，直接子类重写该方法即可
     * @param uploadFile
     * @param listener
     */
    protected void readExcel(UploadFile uploadFile, FixedHeadExcelListener listener) throws Exception {
        try {
            ExcelHelper.previewReadExcel(uploadFile.getStoragePath(), listener);
        } catch (Exception e) {
            log.warn("解析并校验excel文件失败", e);
            if(e instanceof BusinessException){
                throw e;
            }
            else if (V.notEmpty(e.getMessage())) {
                throw new Exception(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 保存文件之后的处理逻辑，如解析excel
     */
    @Override
    protected int extractDataCount(UploadFile uploadFile) throws Exception {
        FixedHeadExcelListener listener = getExcelDataListener();
        listener.setUploadFileUuid(uploadFile.getUuid());
        listener.setPreview(false);
        listener.setRequestParams(super.getParamsMap());
        readAndSaveExcel(uploadFile, listener);
        //最后拦截，如果数据异常在listener中未被拦截抛出异常，此处进行处理
        if (V.notEmpty(listener.getErrorMsgs())) {
            throw new BusinessException(Status.FAIL_VALIDATION, S.join(listener.getErrorMsgs(), "; "));
        }
        return listener.getDataList().size();
    }

    /**
     * 读取并保存excel, 如果需要替换成远程，直接子类重写该方法即可
     * @param uploadFile
     * @throws Exception
     */
    protected void readAndSaveExcel(UploadFile uploadFile, FixedHeadExcelListener listener ) throws Exception {
        try {
            ExcelHelper.readAndSaveExcel(uploadFile.getStoragePath(), listener);
        } catch (Exception e) {
            log.warn("上传数据错误: " + e.getMessage(), e);
            if (V.notEmpty(e.getMessage())) {
                throw new Exception(e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 检查是否为合法的excel文件
     *
     * @param file
     * @throws Exception
     */
    private void checkIsExcel(MultipartFile file) throws Exception {
        if (V.isEmpty(file)) {
            throw new BusinessException(Status.FAIL_INVALID_PARAM, "未获取待处理的excel文件！");
        }
        String fileName = file.getOriginalFilename();
        if (V.isEmpty(fileName) || !FileHelper.isExcel(fileName)) {
            log.debug("非Excel类型: " + fileName);
            throw new BusinessException(Status.FAIL_VALIDATION, "请上传合法的Excel格式文件！");
        }
    }

}
