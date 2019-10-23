package com.diboot.component.excel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.diboot.component.excel.entity.BaseExcelDataEntity;
import com.diboot.component.excel.entity.ExcelColumn;
import com.diboot.component.excel.listener.BaseExcelDataListener;
import com.diboot.component.excel.service.ExcelColumnService;
import com.diboot.component.excel.service.ExcelImportRecordService;
import com.diboot.component.excel.utils.EasyExcelHelper;
import com.diboot.component.file.entity.BaseFile;
import com.diboot.component.file.file.FileHelper;
import com.diboot.component.file.service.BaseFileService;
import com.diboot.core.entity.BaseEntity;
import com.diboot.core.service.BaseService;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel导入基类
 * @author Mazc
 * @version 2017/9/18
 * Copyright @ www.com.ltd
 */
public abstract class BaseExcelImportController <T extends BaseExcelDataEntity> {
    private static final Logger logger = LoggerFactory.getLogger(BaseExcelImportController.class);

    @Autowired
    protected BaseFileService baseFileService;
    @Autowired
    protected ExcelColumnService excelColumnService;
    @Autowired
    protected ExcelImportRecordService excelImportRecordService;

    protected static final String PARAM_IMPORT_UUID = "importUid";

    /**
     * 获取Excel列的定义
     */
    private static Map<String, List<ExcelColumn>> excelColumnMap = null;

    /***
     * 获取Model类
     * @return
     */
    protected abstract Class<?> getModelClass();

    /***
     * 获取ExcelData模板类
     * @return
     */
    protected abstract Class<T> getExcelDataClass();

    /***
     * 获取业务的service
     * @return
     */
    protected abstract BaseService getBusinessService();

    /***
     * 获取对应的ExcelDataListener
     * @return
     */
    protected abstract BaseExcelDataListener getExcelDataListener();

    protected BaseService getService() {
        return baseFileService;
    }

    /***
     * 列表页处理
     * @param request
     * @return
     * @throws Exception
     */
    public JsonResult listPaging(LambdaQueryWrapper<? extends BaseFile> queryWrapper, Pagination pagination, HttpServletRequest request) throws Exception{
        queryWrapper.eq(BaseFile::getRelObjType, getModelClass().getSimpleName());
        // 查询当前页的数据
        List entityList = getService().getEntityList(queryWrapper, pagination);
        // 返回结果
        return new JsonResult(Status.OK, entityList).bindPagination(pagination);
    }

    /***
     * 预览处理
     * @param request
     * @return
     * @throws Exception
     */
    public JsonResult preview(HttpServletRequest request) throws Exception {
        Map dataMap = new HashMap();
        try{
            BaseEntity model = (BaseEntity) getModelClass().newInstance();
            List<T> modelList = saveExcelFile(request, model, true);
            //获取预览时的表头
            List<Map> header = getPreviewDataHeader();
            dataMap.put("header", header);
            //最多返回前端十条数据
            dataMap.put("modelList", modelList.size()>10?modelList.subList(0,10):modelList.subList(0,modelList.size()));
            // 上传文件的id
            dataMap.put(PARAM_IMPORT_UUID, request.getAttribute(PARAM_IMPORT_UUID));
            return new JsonResult(Status.OK, dataMap);
        }
        catch(Exception e){
            logger.warn("预览数据失败 -->" + e);
            return new JsonResult(Status.FAIL_OPERATION, e.getMessage());
        }
    }

    /***
     * 预览保存
     * @param request
     * @param
     * @return
     * @throws Exception
     */
    public JsonResult previewSave(HttpServletRequest request) throws Exception {
        String importUid = request.getParameter(PARAM_IMPORT_UUID);
        if(V.isEmpty(importUid)){
            return new JsonResult(Status.FAIL_OPERATION, "预览保存失败，无法获取上传文件编号 importUid！");
        }
        try{
            LambdaQueryWrapper<BaseFile> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BaseFile::getUuid, importUid);
            BaseFile importFile = baseFileService.getModel(wrapper);
            boolean success = false;
            success = EasyExcelHelper.simpleReadAndSave(FileHelper.getFileStorageDirectory() + importFile.getPath(),getExcelDataClass(),getExcelDataListener());
            if(success){
                // 更新上传文件信息
                importFile.setDeleted(false);
                importFile.setDataCount(getExcelDataListener().getEntityList().size());
                baseFileService.updateModel(importFile);
                // 批量保存导入记录明细
                success = excelImportRecordService.batchCreateRecords(importUid, getExcelDataListener().getEntityList());
                if(!success){
                    logger.warn("数据导入成功，但保存导入历史记录信息失败！fileUuid="+importUid);
                }
            }else{
                logger.error("数据上传失败:"+ S.join(getExcelDataListener().getErrorMsgs(), "<br/>"));
                return new JsonResult(Status.FAIL_OPERATION, S.join(getExcelDataListener().getErrorMsgs(), "<br/>"));
            }
        }
        catch(Exception e){
            logger.error("上传数据错误: "+ e.getMessage(), e);
        }
        return new JsonResult(Status.OK,"上传成功");
    }

    /***
     * 直接上传
     * @param request
     * @param
     * @return
     * @throws Exception
     */
    public JsonResult upload(HttpServletRequest request) throws Exception {
        try{
            BaseEntity model = (BaseEntity) getModelClass().newInstance();
            saveExcelFile(request, model, false);
            String importUid = (String) request.getAttribute(PARAM_IMPORT_UUID);
            // 批量保存导入记录明细
            if(importUid != null){
                boolean success = excelImportRecordService.batchCreateRecords(importUid, getExcelDataListener().getEntityList());
                if(!success){
                    logger.warn("数据导入成功，但保存导入历史记录信息失败！fileUuid="+importUid);
                }
            }
            else{
                logger.warn("数据导入成功，但无法导入历史记录信息: importUuid不存在！");
            }
        }
        catch(Exception e){
            logger.error("上传数据错误: "+ e.getMessage(), e);
            return new JsonResult(Status.FAIL_OPERATION, e.getMessage());
        }
        return new JsonResult(Status.OK);
    }

    /**
     *  保存上传文件
     * @param request
     * @param model
     * @param fileInputName
     * @return
     * @throws Exception
     */
    protected List<T> saveExcelFile(HttpServletRequest request, BaseEntity model, boolean isPreview, String... fileInputName) throws Exception{
        MultipartFile file = FileHelper.getFileFromRequest(request, fileInputName);
        if(V.isEmpty(file)) {
            throw new Exception("未获取待处理的excel文件！");
        }
        List<T> dataList = null;
        String fileName = file.getOriginalFilename();
        if (V.isEmpty(fileName) || !FileHelper.isExcel(fileName)) {
            logger.info("非Excel类型: " + fileName);
            throw new Exception("上传的文件为非Excel文件类型！");
        }
        // 文件后缀
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        // 先保存文件
        String uuid = S.newUuid();
        String newFileName = uuid + "." + ext;
        String path = FileHelper.saveFile(file, newFileName);
        if(V.isEmpty(path)){
            logger.info("文件保存失败");
            throw new Exception("文件保存失败");
        }
        BaseFile fileObj = new BaseFile();
//        fileObj.setUuid(uuid);
        fileObj.setName(fileName);
        fileObj.setRelObjType(model.getClass().getSimpleName());
        fileObj.setRelObjId(model.getId());
        fileObj.setFileType(ext);
        fileObj.setName(fileName);
        fileObj.setPath(path);
        fileObj.setLink("/file/download/" + uuid);
        fileObj.setSize(file.getSize());
        fileObj.setComment(request.getParameter("comment"));
        try {
            dataList = EasyExcelHelper.simpleRead(FileHelper.getFileStorageDirectory() + path, getExcelDataClass(), getExcelDataListener(), isPreview);
        } catch (Exception e) {
            throw new Exception("解析excel文件失败");
        }
        if(V.notEmpty(getExcelDataListener().getErrorMsgs())){
            throw new Exception(S.join(getExcelDataListener().getErrorMsgs(), "<br/>"));
        }
        // 初始设置为0，批量保存数据后更新
        if(isPreview){
            fileObj.setDeleted(true);
            fileObj.setDataCount(0);
        }else{
            fileObj.setDeleted(false);
            fileObj.setDataCount(getExcelDataListener().getEntityList().size());
        }
        baseFileService.createEntity(fileObj);
        // 绑定属性到model
        request.setAttribute(PARAM_IMPORT_UUID, uuid);
        logger.info("成功保存附件: uid=" + uuid + ", name=" + fileName);
        // 返回结果
        return dataList;
    }

    /**
     * 获取预览数据时显示在页面上的数据表头
     * @return
     * @throws Exception
     */
    private List<Map> getPreviewDataHeader() throws Exception{
        List<Map> list = new ArrayList<>();
        List<ExcelColumn> excelColumns = getExcelDataListener().getExcelColumnList(getModelClass().getSimpleName());
        if(V.notEmpty(excelColumns)){
            for(ExcelColumn excelColumn : excelColumns){
                Map map = new HashMap();
                map.put("title",excelColumn.getColName());
                map.put("dataIndex", excelColumn.getModelField());
                list.add(map);
            }
        }
        return list;
    }
}
