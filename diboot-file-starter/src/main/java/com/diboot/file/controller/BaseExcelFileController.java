package com.diboot.file.controller;

import com.diboot.file.entity.UploadFile;
import com.diboot.file.excel.listener.FixedHeadExcelListener;
import com.diboot.file.util.ExcelHelper;
import com.diboot.file.util.FileHelper;
import com.diboot.core.config.BaseConfig;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Status;
import lombok.extern.slf4j.Slf4j;
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
     * @param request
     * @return
     * @throws Exception
     */
    public JsonResult excelPreview(MultipartFile file, HttpServletRequest request) throws Exception {
        Map<String, Object> dataMap = new HashMap();
        savePreviewExcelFile(file, request, dataMap);
        return JsonResult.OK(dataMap);
    }

    /***
     * 预览后提交保存
     * @param request
     * @param
     * @return
     * @throws Exception
     */
    public <T> JsonResult excelPreviewSave(Class<T> entityClass, String previewFileName, String originFileName, HttpServletRequest request) throws Exception {
        if(V.isEmpty(previewFileName) || V.isEmpty(originFileName)){
            throw new BusinessException(Status.FAIL_INVALID_PARAM, "预览保存失败，参数 tempFileName 或 originFileName 未指定！");
        }
        String fileUid = S.substringBefore(previewFileName, ".");
        String fullPath = FileHelper.getFullPath(previewFileName);
        String ext = FileHelper.getFileExtByName(originFileName);
        // 描述
        String description = getString(request, "description");
        // 保存文件上传记录
        UploadFile uploadFile = new UploadFile().setUuid(fileUid)
                .setFileName(originFileName).setStoragePath(fullPath).setFileType(ext)
                .setRelObjType(entityClass.getSimpleName()).setDescription(description);
        super.createUploadFile(uploadFile, request);
        return JsonResult.OK();
    }

    /***
     * 直接上传excel
     * @param request
     * @param
     * @return
     * @throws Exception
     */
    public <T> JsonResult uploadExcelFile(MultipartFile file, Class<T> entityClass, HttpServletRequest request) throws Exception {
        checkIsExcel(file);
        return super.uploadFile(file, entityClass, request);
    }

    /**
     *  保存上传文件
     * @param request
     * @return
     * @throws Exception
     */
    private void savePreviewExcelFile(MultipartFile file, HttpServletRequest request, Map<String, Object> dataMap) throws Exception{
        checkIsExcel(file);
        // 保存文件到本地
        UploadFile uploadFile = super.saveFile(file, getExcelDataListener().getExcelModelClass(), request);
        // 预览
        FixedHeadExcelListener listener = getExcelDataListener();
        listener.setRequestParams(super.getParamsMap(request));
        try {
            ExcelHelper.previewReadExcel(uploadFile.getStoragePath(), listener);
        }
        catch (Exception e) {
            log.warn("解析并校验excel文件失败", e);
            if(V.notEmpty(e.getMessage())){
                throw new Exception(e.getMessage());
            }
            throw e;
        }
        // 绑定属性到model
        dataMap.put("header", listener.getFieldHeaders());
        dataMap.put(ORIGIN_FILE_NAME, file.getOriginalFilename());
        dataMap.put(PREVIEW_FILE_NAME, FileHelper.getFileName(uploadFile.getStoragePath()));
        List dataList = listener.getDataList();
        if(V.notEmpty(dataList) && dataList.size() > BaseConfig.getPageSize()){
            dataList = dataList.subList(0, BaseConfig.getPageSize());
        }
        //最多返回前端十条数据
        dataMap.put("dataList", dataList);
    }

    /**
     * 保存文件之后的处理逻辑，如解析excel
     */
    @Override
    protected int extractDataCount(String fileUuid, String fullPath, HttpServletRequest request) throws Exception{
        FixedHeadExcelListener listener = getExcelDataListener();
        listener.setUploadFileUuid(fileUuid);
        listener.setPreview(false);
        listener.setRequestParams(super.getParamsMap(request));
        try{
            ExcelHelper.readAndSaveExcel(fullPath, listener);
        }
        catch(Exception e){
            log.warn("上传数据错误: "+ e.getMessage(), e);
            if(V.notEmpty(e.getMessage())){
                throw new Exception(e.getMessage());
            }
            throw e;
        }
        return listener.getDataList().size();
    }

    /**
     * 检查是否为合法的excel文件
     * @param file
     * @throws Exception
     */
    private void checkIsExcel(MultipartFile file) throws Exception{
        if(V.isEmpty(file)) {
            throw new BusinessException(Status.FAIL_INVALID_PARAM, "未获取待处理的excel文件！");
        }
        String fileName = file.getOriginalFilename();
        if (V.isEmpty(fileName) || !FileHelper.isExcel(fileName)) {
            log.debug("非Excel类型: " + fileName);
            throw new BusinessException(Status.FAIL_VALIDATION, "请上传合法的Excel格式文件！");
        }
    }

}
