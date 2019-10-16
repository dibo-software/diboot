package com.diboot.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.diboot.component.file.entity.BaseFile;
import com.diboot.component.file.file.FileHelper;
import com.diboot.component.file.vo.JsonResult;
import com.diboot.component.file.vo.Pagination;
import com.diboot.core.entity.Dictionary;
import com.diboot.core.service.BaseService;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.vo.Status;
import com.diboot.example.entity.DictionaryExcelData;
import com.diboot.example.listener.DictionaryExcelDataListener;
import com.diboot.component.excel.controller.BaseExcelImportController;
import com.diboot.component.excel.listener.BaseExcelDataListener;
import com.diboot.component.excel.utils.EasyExcelHelper;
import com.diboot.shiro.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/***
* 角色数据导入相关操作Controller
* @author wangyongliang
* @version 2019-7-11
*/
@RestController
@RequestMapping("/roleImport")
@Scope("prototype")
public class RoleImportController extends BaseExcelImportController {
    private static final Logger logger = LoggerFactory.getLogger(RoleImportController.class);

    @Autowired
    private RoleService roleService;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private DictionaryExcelDataListener dictionaryExcelDataListener;

    @Override
    public Class getModelClass() {
        return Dictionary.class;
    }

    @Override
    protected Class getExcelDataClass() {
        return DictionaryExcelData.class;
    }

    @Override
    public BaseService getBusinessService() {
        return roleService;
    }

    @Override
    protected BaseExcelDataListener getExcelDataListener() {
        return dictionaryExcelDataListener;
    }


    /*
    * 获取文件上传记录（分页）
    * */
    @GetMapping("/list")
    public JsonResult list(Pagination pagination, HttpServletRequest request) throws Exception {
        LambdaQueryWrapper<BaseFile> wrapper = new LambdaQueryWrapper();
        return super.listPaging(wrapper, pagination, request);
    }

    /***
    * 预览数据
    * @throws Exception
    */
    @PostMapping("/preview")
    public JsonResult preview( HttpServletRequest request) throws Exception {
        return super.preview(request);
    }

    /***
    * 保存数据
    * @throws Exception
    */
    @Override
    @PostMapping("/previewSave")
    public JsonResult previewSave(HttpServletRequest request) throws Exception {
        return super.previewSave(request);
    }

    /***
    * 上传excel并保存
    * @throws Exception
    */
    @Override
    @PostMapping("/upload")
    public JsonResult upload(HttpServletRequest request) throws Exception {
        return super.upload(request);
    }

    /*
    * 列表页数据导出
    * */
    @GetMapping("/listExport")
    public JsonResult listExport(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Dictionary> entityList = dictionaryService.getEntityList(null);
        List<DictionaryExcelData> dataList = BeanUtils.convertList(entityList, DictionaryExcelData.class);
        String filePath = FileHelper.getFileStorageDirectory()+"元数据导出.xls";
        //导出数据
        boolean success = EasyExcelHelper.simpleWrite(filePath, DictionaryExcelData.class, dataList);
        if(!success){
            return new JsonResult(Status.FAIL_OPERATION, "文件导出失败");
        }
        FileHelper.downloadLocalFile(filePath, request, response);
        return null;
    }

}