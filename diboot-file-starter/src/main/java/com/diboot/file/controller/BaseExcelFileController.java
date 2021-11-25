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
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Status;
import com.diboot.file.entity.UploadFile;
import com.diboot.file.excel.BaseExcelModel;
import com.diboot.file.excel.listener.FixedHeadExcelListener;
import com.diboot.file.util.ExcelHelper;
import com.diboot.file.util.FileHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
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

    /***
     * 获取对应的ExcelDataListener
     * @return
     */
    protected abstract FixedHeadExcelListener<?> getExcelDataListener();

    /***
     * excel数据预览
     * @return
     * @throws Exception
     */
    public JsonResult excelPreview(MultipartFile file, Class<?> clazz) throws Exception {
        checkIsExcel(file);
        // 保存文件到本地
        UploadFile uploadFile = super.saveFile(file, clazz);
        // 构建预览数据Map
        Map<String, Object> dataMap = buildPreviewDataMap(uploadFile);
        // 保存文件上传记录
        super.createUploadFile(uploadFile);
        return JsonResult.OK(dataMap);
    }


    /**
     * 构建预览数据Map
     *
     * @param uploadFile
     * @return
     * @throws Exception
     */
    private Map<String, Object> buildPreviewDataMap(UploadFile uploadFile) throws Exception {
        Map<String, Object> dataMap = new HashMap<>(8);
        // 预览
        FixedHeadExcelListener<?> listener = getExcelDataListener();
        listener.setPreview(true);
        listener.setRequestParams(super.getParamsMap());
        // 读取excel
        readExcelFile(uploadFile, listener);

        //最后拦截，如果数据异常在listener中未被拦截抛出异常，此处进行处理
        if (V.notEmpty(listener.getExceptionMsgs())) {
            throw new BusinessException(Status.FAIL_VALIDATION, S.join(listener.getExceptionMsgs(), "; "));
        }
        // 绑定属性到model
        dataMap.put("tableHead", listener.getTableHead());
        dataMap.put("uuid", FileHelper.getFileName(uploadFile.getStoragePath()));
        List<?> dataList = listener.getDataList();
        uploadFile.setDataCount(dataList.size());
        int totalCount = 0;
        if (V.notEmpty(dataList)) {
            totalCount = dataList.size();
            if (dataList.size() > BaseConfig.getPageSize()) {
                dataList = dataList.subList(0, BaseConfig.getPageSize());
            }
        }
        //最多返回前端1页数据
        dataMap.put("dataList", dataList);
        dataMap.put("totalCount", totalCount);
        List<?> errorDataList = listener.getErrorDataList();
        if (V.notEmpty(errorDataList)) {
            dataMap.put("errorCount", errorDataList.size());
            dataMap.put("errorMsgs", listener.getErrorMsgs());
        }
        return dataMap;
    }

    /**
     * 预览后提交保存
     *
     * @param
     * @return
     * @throws Exception
     */
    public <T> JsonResult excelPreviewSave(String uuid) throws Exception {
        UploadFile entity = uploadFileService.getEntity(S.substringBefore(uuid, "."));
        FixedHeadExcelListener<?> excelDataListener = getExcelDataListener();
        // 读取
        readExcelFile(entity, excelDataListener);
        Map<String, Object> dataMap = saveErrorFile(entity, excelDataListener);
        List<?> properDataList = excelDataListener.getProperDataList();
        entity.setDataCount(properDataList == null ? 0 : properDataList.size());
        String description = getString("description", "Excel预览导入数据");
        uploadFileService.updateEntity(entity.setDescription(description));
        return JsonResult.OK(dataMap);
    }


    /**
     * 直接上传excel
     *
     * @param
     * @return
     * @throws Exception
     */
    public <T> JsonResult uploadExcelFile(MultipartFile file, Class<T> entityClass) throws Exception {
        checkIsExcel(file);
        // 保存文件到本地
        UploadFile uploadFile = super.saveFile(file, entityClass);

        FixedHeadExcelListener<?> excelDataListener = getExcelDataListener();
        // 读excel
        readExcelFile(uploadFile, excelDataListener);
        Map<String, Object> dataMap = saveErrorFile(uploadFile, excelDataListener);
        uploadFile.setDataCount(excelDataListener.getProperDataList().size());
        String description = getString("description", "Excel导入数据");
        uploadFileService.createEntity(uploadFile.setDescription(description));
        return JsonResult.OK(dataMap);
    }

    /**
     * 保存错误数据
     *
     * @param uploadFile
     * @param excelDataListener
     * @param <T>
     * @return
     */
    private <T extends BaseExcelModel> Map<String, Object> saveErrorFile(UploadFile uploadFile, FixedHeadExcelListener<T> excelDataListener) {
        List<T> errorDataList = excelDataListener.getErrorDataList();
        if (V.isEmpty(errorDataList)) {
            return Collections.emptyMap();
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ExcelHelper.write(outputStream, excelDataListener.getExcelModelClass(), excelDataListener.getFieldNameMap().values(), errorDataList);
        String[] split = uploadFile.getFileName().split("\\.");
        try {
            UploadFile errorFile = saveFile(new ByteArrayInputStream(outputStream.toByteArray()), split[0] + "_错误数据." + split[1]);
            errorFile.setRelObjType(uploadFile.getRelObjType());
            errorFile.setDataCount(errorDataList.size());
            errorFile.setDescription(uploadFile.getFileName() + " - 错误数据");
            super.createUploadFile(errorFile);
            return new HashMap<String, Object>(8) {{
                put("totalCount", excelDataListener.getDataList().size());
                put("errorUrl", errorFile.getAccessUrl());
                put("errorCount", errorDataList.size());
                put("errorMsgs", excelDataListener.getErrorMsgs());
            }};
        } catch (Exception e) {
            log.error("保存错误数据异常", e);
            return Collections.emptyMap();
        }
    }


    /**
     * 读取excel方法，如果需要替换成远程，直接子类重写该方法即可
     *
     * @param uploadFile
     * @param listener
     */
    protected <T extends BaseExcelModel> void readExcelFile(UploadFile uploadFile, FixedHeadExcelListener<T> listener) throws Exception {
        try {
            ExcelTypeEnum excelType = "csv".equalsIgnoreCase(uploadFile.getFileType()) ? ExcelTypeEnum.CSV : null;
            ExcelHelper.read(fileStorageService.getFile(uploadFile.getStoragePath()), excelType, listener, listener.getExcelModelClass());
        } catch (Exception e) {
            log.warn("解析excel文件失败", e);
            if (e instanceof BusinessException) {
                throw e;
            } else if (V.notEmpty(e.getMessage())) {
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
    private void checkIsExcel(MultipartFile file) {
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
