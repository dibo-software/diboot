/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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
