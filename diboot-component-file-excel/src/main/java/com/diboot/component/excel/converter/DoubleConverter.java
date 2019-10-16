package com.diboot.component.excel.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.math.BigDecimal;

public class DoubleConverter implements Converter<Double> {
    @Override
    public Class supportJavaTypeKey() {
        return Double.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.NUMBER;
    }

    @Override
    public Double convertToJavaData(CellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
       Double value = null;
        String colName = contentProperty.getHead().getHeadNameList().get(0);//当前列
        try {
            value = cellData.getNumberValue().doubleValue();
        } catch (Exception e) {
            throw new Exception("["+colName+"]列数据格式有误，请填写正确的浮点型数据");
        }
        return value;
    }

    @Override
    public CellData convertToExcelData(Double value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return new CellData(BigDecimal.valueOf(value));
    }
}
