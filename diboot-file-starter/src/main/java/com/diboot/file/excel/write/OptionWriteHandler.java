/*
 * Copyright (c) 2015-2021, www.dibo.ltd (service@dibo.ltd).
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

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.diboot.core.exception.InvalidUsageException;
import com.diboot.core.service.DictionaryServiceExtProvider;
import com.diboot.core.util.AnnotationUtils;
import com.diboot.core.util.ContextHolder;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.LabelValue;
import com.diboot.file.excel.annotation.ExcelOption;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;

/**
 * Excel写入 下拉选项 Handler
 *
 * @author wind
 * @version v2.3.0
 */
@Slf4j
public class OptionWriteHandler implements CellWriteHandler {

    /**
     * 设置单元格下拉框选项
     */
    @Override
    public void beforeCellCreate(CellWriteHandlerContext context) {
        Sheet sheet = context.getWriteSheetHolder().getSheet();
        if (sheet.getLastRowNum() != 0) {
            return;
        }
        Head head = context.getHeadData();
        ExcelOption option = AnnotationUtils.getAnnotation(head.getField(), ExcelOption.class);
        if (option == null) {
            return;
        }
        String[] options = null;
        String dictType = option.dict();
        if (S.isNotEmpty(dictType)) {
            options = getDictOptions(dictType);
        }
        if (V.isEmpty(options)) {
            options = option.options();
        }
        // 下拉框选为空时不添加 单元格验证（单元下拉选项）
        if (V.isEmpty(options)) {
            return;
        }
        // 单元格范围地址
        int col = context.getColumnIndex();
        int rows = option.rows();
        int firstRow = -1;
        int lastRow = rows > 0 ? (firstRow = head.getHeadNameList().size()) - 1 + rows : -1;
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, col, col);
        // 设置数据验证
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = helper.createExplicitListConstraint(options);
        DataValidation dataValidation = helper.createValidation(constraint, addressList);
        dataValidation.setErrorStyle(option.errorStyle());
        // 处理Excel兼容性问题
        if (dataValidation instanceof XSSFDataValidation) {
            dataValidation.setSuppressDropDownArrow(true);
            dataValidation.setShowErrorBox(true);
        } else {
            dataValidation.setSuppressDropDownArrow(false);
        }
        sheet.addValidationData(dataValidation);
    }

    /**
     * 从字典中获取选项
     *
     * @param dictType 字典类型
     * @return 选项数组
     */
    protected String[] getDictOptions(String dictType) {
        DictionaryServiceExtProvider bindDictService = ContextHolder.getBean(DictionaryServiceExtProvider.class);
        if (bindDictService == null) {
            throw new InvalidUsageException("exception.invalidUsage.optionWriteHandler.getDictOptions.message");
        }
        String[] options = bindDictService.getLabelValueList(dictType).stream().map(LabelValue::getLabel).toArray(String[]::new);
        if (V.isEmpty(options)) {
            log.warn("@ExcelOption 关联字典: {} 无值", dictType);
        }
        return options;
    }
}
