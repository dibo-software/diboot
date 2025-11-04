package com.example.api.excel.model;

import cn.idev.excel.annotation.ExcelProperty;
import com.diboot.file.excel.BaseExcelModel;
import com.diboot.file.excel.annotation.ExcelOption;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户导出model定义
 *
 * @author MyName
 * @version 1.0
 * @date 2022-12-30
 * Copyright © MyCompany
 */
@Getter
@Setter
public class UserExportModel extends BaseExcelModel {

    @ExcelProperty(value = "姓名")
    private String realname;

    @ExcelProperty(value = "用户编号")
    private String userNum;

    @ExcelOption(dict = "GENDER")
    @ExcelProperty(value = "性别")
    private String gender;

    @ExcelProperty(value = "电话")
    private String mobilePhone;

    @ExcelProperty(value = "邮箱")
    private String email;
}