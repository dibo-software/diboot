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

import com.diboot.core.controller.BaseController;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Status;
import com.diboot.file.entity.FileRecord;
import com.diboot.file.excel.BaseExcelModel;
import com.diboot.file.excel.listener.ReadExcelListener;
import com.diboot.file.service.FileRecordService;
import com.diboot.file.service.FileStorageService;
import com.diboot.file.util.ExcelHelper;
import com.diboot.file.util.FileHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
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
public abstract class BaseExcelFileController extends BaseController {

    @Autowired
    protected FileRecordService fileRecordService;

    @Autowired
    protected FileStorageService fileStorageService;

    /**
     * 获取对应的ExcelDataListener
     */
    protected abstract ReadExcelListener<?> getExcelDataListener();

    /**
     * excel数据预览
     *
     * @param file   excel文件
     * @param params 请求参数
     * @return
     * @throws Exception
     */
    public JsonResult<Map<String, Object>> excelPreview(MultipartFile file, Map<String, Object> params) throws Exception {
        checkIsExcel(file);
        // 保存文件
        FileRecord fileRecord = fileStorageService.save(file);
        fileRecord.setDescription(params.compute("description", (k, v) -> S.defaultIfEmpty(S.valueOf(v), "Excel预览数据")).toString());

        ReadExcelListener<?> listener = getExcelDataListener();
        listener.setImportFileUuid(fileRecord.getId());
        listener.setRequestParams(params);
        // 预览
        listener.setPreview(true);
        // 读取excel
        readExcelFile(file.getInputStream(), listener);
        // 保存文件上传记录
        fileRecordService.createEntity(fileRecord);

        Map<String, Object> dataMap = new HashMap<>(8);
        dataMap.put("id", fileRecord.getId());
        dataMap.put("tableHeads", listener.getTableHeads());
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
     * @param params 请求参数；必需包含预览返回的文件uuid
     * @return
     * @throws Exception
     */
    public JsonResult<Map<String, Object>> excelPreviewSave(Map<String, Object> params) throws Exception {
        String uuid = params.get("id").toString();
        if (V.isEmpty(uuid)) {
            throw new BusinessException("exception.business.excel.unknownPreviewSave");
        }
        FileRecord uploadFile = fileRecordService.getEntity(uuid);
        uploadFile.setDescription(params.compute("description", (k, v) -> S.defaultIfEmpty(S.valueOf(v), "Excel预览后导入数据")).toString());
        return importData(uploadFile, fileStorageService.getFile(uploadFile.getStoragePath()), params);
    }


    /**
     * 直接上传excel
     *
     * @param file   excel文件
     * @param params 请求参数
     * @return
     * @throws Exception
     */
    public JsonResult<Map<String, Object>> uploadExcelFile(MultipartFile file, Map<String, Object> params) throws Exception {
        checkIsExcel(file);
        // 保存文件
        FileRecord fileStorage = fileStorageService.save(file);
        fileStorage.setDescription(params.compute("description", (k, v) -> S.defaultIfEmpty(S.valueOf(v), "Excel导入数据")).toString());
        fileRecordService.createEntity(fileStorage);
        return importData(fileStorage, file.getInputStream(), params);
    }

    /**
     * 导入数据
     *
     * @param uploadFile  上传文件对象
     * @param inputStream excel文件输入流
     * @param params      请求参数
     * @return
     * @throws Exception
     */
    private JsonResult<Map<String, Object>> importData(FileRecord uploadFile, InputStream inputStream,
                                                       Map<String, Object> params) throws Exception {
        ReadExcelListener<?> listener = getExcelDataListener();
        listener.setImportFileUuid(uploadFile.getId());
        listener.setRequestParams(params);
        // 读excel
        readExcelFile(inputStream, listener);
        fileRecordService.updateEntity(uploadFile);

        String errorDataFilePath = listener.getErrorDataFilePath();
        if (errorDataFilePath == null) {
            return JsonResult.OK();
        }
        String errorDataFileName = uploadFile.getFileName().replaceFirst("\\.\\w+$", "_错误数据.xlsx");
        File errDataFile = new File(errorDataFilePath);
        long fileSize = errDataFile.length();
        FileRecord errorFile;
        if (FileHelper.isLocalStorage()) {
            String errorDataFileUidName = S.substringAfterLast(errorDataFilePath, "/");
            String errorDataFileUid = S.substringBefore(errorDataFileUidName, ".");
            errorFile = new FileRecord(errorDataFileUid)
                    .setFileType("xlsx")
                    .setFileName(errorDataFileName)
                    .setFileSize(fileSize)
                    .setStoragePath(errorDataFilePath)
                    .setAccessUrl(fileStorageService.buildAccessUrl(errorDataFileUidName, ""));
        } else {
            errorFile = fileStorageService.save(Files.newInputStream(errDataFile.toPath()), errorDataFileName, fileSize);
            FileHelper.deleteFile(errorDataFilePath);
        }
        errorFile.setDescription(uploadFile.getFileName() + " - 错误数据");
        // 创建异常数据记录
        fileRecordService.createEntity(errorFile);
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
     * @param inputStream
     * @param listener
     */
    protected <T extends BaseExcelModel> void readExcelFile(InputStream inputStream, ReadExcelListener<T> listener) throws Exception {
        try {
            ExcelHelper.read(inputStream, listener, listener.getExcelModelClass());
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
    protected void checkIsExcel(MultipartFile file) {
        if (V.isEmpty(file)) {
            throw new BusinessException(Status.FAIL_INVALID_PARAM, "exception.business.excel.noFile");
        }
        String fileName = file.getOriginalFilename();
        if (V.isEmpty(fileName) || !FileHelper.isExcel(fileName)) {
            log.debug("非Excel类型: {}", fileName);
            throw new BusinessException(Status.FAIL_VALIDATION, "exception.business.excel.formatError");
        }
        if (file.isEmpty()) {
            log.debug("空文件：{}", fileName);
            throw new BusinessException(Status.FAIL_VALIDATION, "exception.business.excel.emptyContent");
        }
    }
}
