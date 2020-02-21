package com.diboot.component.file.excel.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * excel解析 Converter
 * @auther wangyl@dibo.ltd
 * @date 2019-10-9
 */
public class BooleanConverter implements Converter<Boolean> {
    private static final Logger log = LoggerFactory.getLogger(BooleanConverter.class);

    @Override
    public Class supportJavaTypeKey() {
        return Boolean.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.BOOLEAN;
    }

    @Override
    public Boolean convertToJavaData(CellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        Boolean value = null;
        String colName = contentProperty.getHead().getHeadNameList().get(0);//当前列
        if(!CellDataTypeEnum.BOOLEAN.equals(cellData.getType())){
            log.warn("["+colName+"]列数据格式有误，请填写正确的逻辑类型数据");
            throw new Exception("["+colName+"]列数据格式有误，请填写正确的逻辑类型数据");
        }
        try {
            value = cellData.getBooleanValue();
        } catch (Exception e) {
            log.warn("["+colName+"]列数据格式有误，请填写正确的逻辑类型数据", e);
            throw new Exception("["+colName+"]列数据格式有误，请填写正确的逻辑类型数据");
        }
        return value;
    }

    @Override
    public CellData convertToExcelData(Boolean value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return new CellData(value);
    }
}
