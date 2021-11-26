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

import com.alibaba.excel.support.ExcelTypeEnum;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Status;
import com.diboot.file.entity.UploadFile;
import com.diboot.file.excel.BaseExcelModel;
import com.diboot.file.excel.listener.ReadExcelListener;
import com.diboot.file.util.ExcelHelper;
import com.diboot.file.util.FileHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
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

    /**
     * 获取对应的ExcelDataListener
     */
    protected abstract ReadExcelListener<?> getExcelDataListener();

    /**
     * excel数据预览
     *
     * @return
     * @throws Exception
     */
    public JsonResult excelPreview(MultipartFile file, Class<?> clazz) throws Exception {
        checkIsExcel(file);
        // 保存文件到本地
        UploadFile uploadFile = super.saveFile(file, clazz);
        uploadFile.setDescription(getString("description", "Excel预览数据"));

        ReadExcelListener<?> listener = getExcelDataListener();
        // 预览
        listener.setPreview(true);
        // 读取excel
        readExcelFile(uploadFile, listener);

        uploadFile.setDataCount(listener.getTotalCount());
        // 保存文件上传记录
        super.createUploadFile(uploadFile);

        Map<String, Object> dataMap = new HashMap<>(8);
        dataMap.put("tableHead", listener.getTableHead());
        dataMap.put("uuid", FileHelper.getFileName(uploadFile.getStoragePath()));
        dataMap.put("dataList", listener.getPreviewDataList());
        dataMap.put("totalCount", listener.getTotalCount());
        if (listener.getErrorCount() > 0) {
            dataMap.put("errorCount", listener.getErrorCount());
            dataMap.put("errorMsgs", listener.getErrorMsgs());
        }
        return JsonResult.OK(dataMap);
    }

    /**
     * 预览后提交保存
     *
     * @param
     * @return
     * @throws Exception
     */
    public <T> JsonResult excelPreviewSave(String uuid) throws Exception {
        UploadFile uploadFile = uploadFileService.getEntity(S.substringBefore(uuid, "."));
        return importData(uploadFile, false);
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
        // 保存文件
        UploadFile uploadFile = super.saveFile(file, entityClass);
        return importData(uploadFile, true);
    }

    private JsonResult<Map<String, Object>> importData(UploadFile uploadFile, boolean isDirectUpload) throws Exception {
        ReadExcelListener<?> listener = getExcelDataListener();
        // 读excel
        readExcelFile(uploadFile, listener);

        if (isDirectUpload) {
            uploadFile.setDataCount(listener.getTotalCount());
            uploadFile.setDescription(getString("description", "Excel导入数据"));
            super.createUploadFile(uploadFile);
        } else {
            uploadFile.setDataCount(listener.getProperCount());
            uploadFile.setDescription(getString("description", "Excel预览后导入数据"));
            uploadFileService.updateEntity(uploadFile);
        }

        UploadFile errorFile = listener.getErrorFile();
        if (errorFile == null) {
            return JsonResult.OK();
        }
        errorFile.setAccessUrl(buildAccessUrl(errorFile.getUuid()))
                .setRelObjType(uploadFile.getRelObjType())
                .setDescription(uploadFile.getFileName() + " - 错误数据");
        super.createUploadFile(errorFile);
        return JsonResult.OK(new HashMap<String, Object>() {{
            put("totalCount", listener.getTotalCount());
            put("errorUrl", errorFile.getAccessUrl());
            put("errorCount", listener.getErrorCount());
            put("errorMsgs", listener.getErrorMsgs());
        }});
    }


    /**
     * 读取excel方法
     *
     * @param uploadFile
     * @param listener
     */
    protected <T extends BaseExcelModel> void readExcelFile(UploadFile uploadFile, ReadExcelListener<T> listener) throws Exception {
        listener.setRequestParams(super.getParamsMap());
        listener.setUploadFileUuid(uploadFile.getUuid());
        listener.setUploadFileName(uploadFile.getFileName());
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
