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
package com.diboot.file.excel.write;

import com.alibaba.excel.exception.ExcelCommonException;
import com.alibaba.excel.write.handler.AbstractSheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.service.DictionaryServiceExtProvider;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.V;
import com.diboot.core.vo.KeyValue;
import com.diboot.core.vo.Status;
import com.diboot.file.excel.annotation.ExcelOption;
import com.diboot.file.excel.cache.ExcelBindOptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;

import java.util.Map;

/**
 * Excel写入 下拉选项 Handler
 *
 * @author wind
 * @version v2.3.0
 * @date 2021-05-29 22:33
 */
@Slf4j
public class OptionWriteHandler extends AbstractSheetWriteHandler {

    /**
     * ExcelModel
     */
    private final Class<?> clazz;

    /**
     * 构造方法
     *
     * @param clazz ExcelModel
     */
    public OptionWriteHandler(Class<?> clazz) {
        this.clazz = clazz;
    }

    /**
     * Sheet页创建之后
     */
    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Map<Integer, ExcelOption> mapDropDown = ExcelBindOptionHandler.getOptionsAnnotationMap(clazz);
        Sheet sheet = writeSheetHolder.getSheet();
        // 开始设置下拉框
        DataValidationHelper helper = sheet.getDataValidationHelper();
        for (Map.Entry<Integer, ExcelOption> entry : mapDropDown.entrySet()) {
            ExcelOption option = entry.getValue();
            int index = entry.getKey();
            // 起始行、终止行、起始列、终止列
            CellRangeAddressList addressList = new CellRangeAddressList(1, option.rows(), index, index);
            // 设置下拉框数据
            DataValidationConstraint constraint = helper.createExplicitListConstraint(getOptions(option));
            DataValidation dataValidation = helper.createValidation(constraint, addressList);
            // 处理Excel兼容性问题
            if (dataValidation instanceof XSSFDataValidation) {
                dataValidation.setSuppressDropDownArrow(true);
                dataValidation.setShowErrorBox(true);
            } else {
                dataValidation.setSuppressDropDownArrow(false);
            }
            sheet.addValidationData(dataValidation);
        }
    }

    /**
     * 获取选项String数组
     *
     * @param option Excel选项注解
     * @return String数组
     */
    private String[] getOptions(ExcelOption option) {
        String[] options = option.value();
        if (options.length > 0) {
            return options;
        }
        String dict = option.dict();
        if (V.isEmpty(dict)) {
            log.error(clazz.getSimpleName() + "@ExcelOption 必须指定 value 或 字典");
            throw new ExcelCommonException("@ExcelOption 必须指定 value 或 字典");
        }
        DictionaryServiceExtProvider bindDictService = ContextHelper.getBean(DictionaryServiceExtProvider.class);
        if (bindDictService == null) {
            throw new BusinessException(Status.FAIL_SERVICE_UNAVAILABLE, "DictionaryService未实现，无法使用ExcelBindDict注解！");
        }
        options = bindDictService.getKeyValueList(dict).stream().map(KeyValue::getK).toArray(String[]::new);
        if (V.isEmpty(options)) {
            log.error(clazz.getSimpleName() + "@ExcelOption 必须指定 value 或 字典");
            throw new ExcelCommonException("@ExcelOption 关联字典: " + dict + " 无值");
        }
        return options;
    }
}
