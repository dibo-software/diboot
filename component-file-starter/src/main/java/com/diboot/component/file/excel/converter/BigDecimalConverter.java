package com.diboot.component.file.excel.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/***
 * excel解析 Converter
 * @auther wangyl@dibo.ltd
 * @date 2019-10-9
 */
public class BigDecimalConverter implements Converter<BigDecimal> {

    private static final Logger log = LoggerFactory.getLogger(BigDecimalConverter.class);

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
        //当前列
        String colName = contentProperty.getHead().getHeadNameList().get(0);
        BigDecimal value = null;
        try {
            value = cellData.getNumberValue();
        } catch (Exception e) {
            log.warn("["+colName+"]列数据格式有误，请填写正确的浮点型数据", e);
            throw new Exception("["+colName+"]列数据格式有误，请填写正确的浮点型数据");
        }
        return value;
    }

    @Override
    public CellData convertToExcelData(BigDecimal value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return new CellData(value);
    }
}
