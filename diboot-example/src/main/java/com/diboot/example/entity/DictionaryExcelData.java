package com.diboot.example.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.diboot.component.excel.converter.StringConverter;
import com.diboot.component.excel.entity.BaseExcelDataEntity;
import lombok.Data;
/*
* dictionary数据导入导出实体类
* */
@Data
public class DictionaryExcelData extends BaseExcelDataEntity {

    @ExcelProperty(value = "类型", converter = StringConverter.class)
    private String type;

    @ExcelProperty(value = "名称", converter = StringConverter.class)
    private String itemName;

    @ExcelProperty(value = "值", converter = StringConverter.class)
    private String itemValue;

}
