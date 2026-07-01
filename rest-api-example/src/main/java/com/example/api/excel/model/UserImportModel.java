package com.example.api.excel.model;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.HeadFontStyle;
import cn.idev.excel.annotation.write.style.HeadStyle;
import cn.idev.excel.enums.poi.FillPatternTypeEnum;
import com.diboot.file.excel.BaseExcelModel;
import com.diboot.file.excel.annotation.ExcelBindDict;
import com.diboot.file.excel.annotation.ExcelBindField;
import com.diboot.file.excel.annotation.ExcelComment;
import com.diboot.file.excel.annotation.ExcelOption;
import com.diboot.iam.entity.IamRole;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


/**
 * 用户导入model定义
 *
 * @author MyName
 * @version 1.0
 * @date 2022-12-30
 * Copyright © MyCompany
 */
@Getter
@Setter
@ExcelIgnoreUnannotated
@HeadFontStyle(fontHeightInPoints = 14)
@HeadStyle(fillPatternType = FillPatternTypeEnum.NO_FILL, fillForegroundColor = 1)
public class UserImportModel extends BaseExcelModel {

    @NotNull(message = "姓名不能为空")
    @HeadStyle(fillForegroundColor = 43)
    @ExcelProperty(value = "姓名")
    private String realname;

    @NotNull(message = "用户编号不能为空")
    @HeadStyle(fillForegroundColor = 43)
    @ExcelProperty(value = "用户编号")
    private String userNum;

    @NotNull(message = "性别不能为空")
    @HeadStyle(fillForegroundColor = 43)
    @ExcelOption(dict = "GENDER")
    @ExcelBindDict(type = "GENDER")
    @ExcelProperty(value = "性别")
    private String gender;

    @ExcelProperty(value = "电话")
    private String mobilePhone;

    @ExcelProperty(value = "邮箱")
    private String email;

    @ExcelBindField(entity = IamRole.class, field = "name", setIdField = "roleId")
    @ExcelProperty("角色")
    private String role;

    private String roleId;

    @ExcelProperty("用户名")
    private String username;

    @ExcelComment("密码为空时无法创建账户")
    @ExcelProperty("密码")
    private String password;
}