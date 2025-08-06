package com.example.api.controller.iam;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.D;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Status;
import com.diboot.file.controller.BaseExcelFileController;
import com.diboot.file.excel.listener.ReadExcelListener;
import com.diboot.file.util.ExcelHelper;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.annotation.OperationCons;
import com.diboot.iam.dto.IamUserSearchDTO;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.service.IamUserService;
import com.diboot.iam.vo.IamUserVO;
import com.example.api.excel.listener.UserImportExcelListener;
import com.example.api.excel.model.UserExportModel;
import com.example.api.excel.model.UserImportModel;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 用户Excel上传下载Controller
 *
 * @author MyName
 * @version 1.0
 * @date 2022-12-30
 * Copyright © MyCompany
 */
@RestController
@RequestMapping("/iam/user/excel")
@Slf4j
@BindPermission(name = "用户Excel上传下载", code = "IamUserExcel")
public class UserExcelController extends BaseExcelFileController {

    @Autowired
    private IamUserService iamUserService;

    /**
     * 下载示例文件
     *
     * @throws Exception
     */
    @BindPermission(name = "下载导入示例文件", code = OperationCons.CODE_IMPORT)
    @GetMapping("/download-example")
    public void downloadExample(HttpServletResponse response) throws Exception {
        String fileName = "用户导入示例.xlsx";
        ExcelHelper.exportExcel(response, fileName, UserImportModel.class, null);
    }

    /**
     * 预览数据
     * @throws Exception
     */
    @Log(operation = "Excel预览", businessObj = "IamUser")
    @BindPermission(name = "Excel导入预览", code = OperationCons.CODE_IMPORT)
    @PostMapping("/preview")
    public JsonResult preview(@RequestParam("file") MultipartFile file, @RequestParam Map<String, Object> params) throws Exception {
        return super.excelPreview(file, params);
    }

    /**
     * 预览保存
     * @throws Exception
     */
    @Log(operation = "Excel预览导入用户", businessObj = "IamUser")
    @BindPermission(name = OperationCons.LABEL_IMPORT, code = OperationCons.CODE_IMPORT)
    @PostMapping("/preview-save")
    public JsonResult previewSave(@RequestParam Map<String, Object> params) throws Exception {
        return super.excelPreviewSave(params);
    }

    /**
     * excel导入用户
     * @throws Exception
     */
    @Log(operation = "Excel导入用户", businessObj = "IamUser")
    @BindPermission(name = OperationCons.LABEL_IMPORT, code = OperationCons.CODE_IMPORT)
    @PostMapping("/upload")
    public JsonResult upload(@RequestParam("file") MultipartFile file, @RequestParam Map<String, Object> params) throws Exception {
        return super.uploadExcelFile(file, params);
    }

    /**
     * 人员列表导出
     * @param iamUserDto
     * @return
     * @throws Exception
     */
    @Log(operation = "导出用户列表", businessObj = "IamUser")
    @BindPermission(name = OperationCons.LABEL_EXPORT, code = OperationCons.CODE_EXPORT)
    @GetMapping("/export")
    public JsonResult export(IamUserSearchDTO iamUserDto, @RequestParam(value = "columns", required = false) List<String> columns, HttpServletResponse response) throws Exception {
        QueryWrapper<IamUser> queryWrapper = super.buildQueryWrapperByDTO(iamUserDto);
        queryWrapper.lambda().orderByAsc(IamUser::getSortId);
        List<IamUserVO> iamUserList = iamUserService.getViewObjectList(queryWrapper, null, IamUserVO.class);
        if (V.isEmpty(iamUserList)) {
            return new JsonResult(Status.FAIL_OPERATION, "用户列表为空，导出失败");
        }
        String fileName = "用户列表导出_" + D.today() + ".xlsx";
        List<UserExportModel> dataList = this.entityList2ExcelList(iamUserList);
        ExcelHelper.exportExcel(response, fileName, UserExportModel.class, columns, dataList);
        return null;
    }

    /**
     * 可导出字段（用于选择导出列）
     * @return
     */
    @GetMapping("/export-table-head")
    public JsonResult getTableHeaderMap() {
        return new JsonResult<>(ExcelHelper.getTableHeads(UserExportModel.class));
    }

    /**
     * 实体列表转excel列表
     * @param userVoList
     * @return
     */
    private List<UserExportModel> entityList2ExcelList(List<IamUserVO> userVoList) {
        if (V.isEmpty(userVoList)) {
            return Collections.emptyList();
        }
        List<UserExportModel> excelModelList = new ArrayList<>();
        for (IamUserVO vo : userVoList) {
            UserExportModel excelModel = new UserExportModel();
            BeanUtils.copyProperties(vo, excelModel);
            if(V.notEmpty(vo.getGenderLabel())) {
                excelModel.setGender(vo.getGenderLabel().getLabel());
            }
            excelModelList.add(excelModel);
        }
        return excelModelList;
    }

    @Override
    protected ReadExcelListener<UserImportModel> getExcelDataListener() {
        return new UserImportExcelListener();
    }

}