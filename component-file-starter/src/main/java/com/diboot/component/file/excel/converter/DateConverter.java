package com.diboot.component.file.excel.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.alibaba.excel.util.DateUtils;
import org.apache.poi.ss.usermodel.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/***
 * excel解析 Converter
 * @auther wangyl@dibo.ltd
 * @date 2019-10-9
 */
public class DateConverter implements Converter<Date> {
    private static final Logger log = LoggerFactory.getLogger(DateConverter.class);

    @Override
    public Class supportJavaTypeKey() {
        return Date.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.NUMBER;
    }

    @Override
    public Date convertToJavaData(CellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        Date value = null;
        String colName = contentProperty.getHead().getHeadNameList().get(0);//当前列
        try {
            value = DateUtil.getJavaDate(cellData.getNumberValue().doubleValue(),
                    globalConfiguration.getUse1904windowing(), null);
        } catch (Exception e) {
            log.warn("["+colName+"]列数据格式有误，请填写正确的时间类型数据", e);
            throw new Exception("["+colName+"]列数据格式有误，请填写正确的时间类型数据");
         }
        return value;
    }

    @Override
    public CellData convertToExcelData(Date value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        if (contentProperty == null || contentProperty.getDateTimeFormatProperty() == null) {
            return new CellData(DateUtils.format(value, null));
        } else {
            return new CellData(DateUtils.format(value, contentProperty.getDateTimeFormatProperty().getFormat()));
        }
    }
}
