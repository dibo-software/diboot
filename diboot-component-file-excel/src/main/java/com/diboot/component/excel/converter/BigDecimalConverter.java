package com.diboot.component.excel.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.math.BigDecimal;

public class BigDecimalConverter implements Converter<BigDecimal> {
    @Override
    public Class supportJavaTypeKey() {
        return BigDecimal.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.NUMBER;
    }

    @Override
    public BigDecimal convertToJavaData(CellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        BigDecimal value = null;
        String colName = contentProperty.getHead().getHeadNameList().get(0);//当前列
        try {
            value = cellData.getNumberValue();
        } catch (Exception e) {
            throw new Exception("["+colName+"]列数据格式有误，请填写正确的浮点型数据");
        }
        return value;
    }

    @Override
    public CellData convertToExcelData(BigDecimal value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return new CellData(value);
    }
}
