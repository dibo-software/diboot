package com.diboot.file.excel.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.diboot.core.binding.annotation.BindDict;
import com.diboot.core.exception.BusinessException;
import com.diboot.file.excel.cache.DictTempCache;

/**
 * 枚举转化器
 *
 * @author : uu
 * @version : v1.0
 * @Date 2020-01-07  16:53
 */
public class DictConverter implements Converter<String> {

    @Override
    public Class supportJavaTypeKey() {
        return DictConverter.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public String convertToJavaData(CellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        // 注解
        BindDict bindDict = contentProperty.getField().getAnnotation(BindDict.class);
        if(bindDict == null){
            throw new BusinessException("DictConverter依赖BindDict注解，请指定");
        }
        String dictValue = DictTempCache.getDictValue(bindDict.type(), cellData.getStringValue());
        if(dictValue == null){
            throw new BusinessException("'"+cellData.getStringValue()+"' 无匹配字典定义");
        }
        return dictValue;
    }

    @Override
    public CellData convertToExcelData(String value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        // 注解
        BindDict bindDict = contentProperty.getField().getAnnotation(BindDict.class);
        if(bindDict == null){
            throw new BusinessException("DictConverter依赖BindDict注解，请指定.");
        }
        return new CellData(DictTempCache.getDictLabel(bindDict.type(), value));
    }
}
