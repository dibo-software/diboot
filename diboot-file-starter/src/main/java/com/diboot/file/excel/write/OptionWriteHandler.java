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

import com.alibaba.excel.util.ClassUtils;
import com.alibaba.excel.write.handler.AbstractSheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.service.DictionaryServiceExtProvider;
import com.diboot.core.util.AnnotationUtils;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.V;
import com.diboot.core.vo.KeyValue;
import com.diboot.core.vo.Status;
import com.diboot.file.excel.annotation.ExcelOption;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;

/**
 * Excel写入 下拉选项 Handler
 *
 * @author wind
 * @version v2.3.0
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
     * Sheet页创建之后，设置单元格下拉框选项
     */
    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Sheet sheet = writeSheetHolder.getSheet();
        // 开始设置下拉框
        DataValidationHelper helper = sheet.getDataValidationHelper();
        setCellOption(clazz, (addressList, options) -> {
            // 设置下拉框数据
            DataValidationConstraint constraint = helper.createExplicitListConstraint(options);
            DataValidation dataValidation = helper.createValidation(constraint, addressList);
            // 处理Excel兼容性问题
            if (dataValidation instanceof XSSFDataValidation) {
                dataValidation.setSuppressDropDownArrow(true);
                dataValidation.setShowErrorBox(true);
            } else {
                dataValidation.setSuppressDropDownArrow(false);
            }
            sheet.addValidationData(dataValidation);
        });
    }

    /**
     * 设置单元格下拉框选项
     *
     * @param clazz  ExcelModel.class
     * @param action 设置单元格下拉框选项的行动
     */
    private void setCellOption(Class<?> clazz, BiConsumer<CellRangeAddressList, String[]> action) {
        Map<Integer, Field> sortedAllFiledMap = new TreeMap<>();
        // 获取 Excel 中的字段（排序后的）
        ClassUtils.declaredFields(clazz, sortedAllFiledMap, null, false, null);
        for (Map.Entry<Integer, Field> entry : sortedAllFiledMap.entrySet()) {
            ExcelOption option = AnnotationUtils.getAnnotation(entry.getValue(), ExcelOption.class);
            // 行数不大于0时不添加 单元格验证（单元下拉选项）
            if (option != null && option.rows() > 0) {
                String[] options = option.options();
                String dictType = option.dict();
                if (options.length == 0 || V.notEmpty(dictType)) {
                    options = getDictOptions(dictType);
                }
                // 下拉框选为空时不添加 单元格验证（单元下拉选项）
                if (V.notEmpty(options)) {
                    int col = entry.getKey();
                    int firstRow = option.firstRow();
                    int lastRow = firstRow - 1 + option.rows();
                    // 创建单元格范围 —— 起始行、终止行、起始列、终止列
                    CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, col, col);
                    action.accept(addressList, options);
                }
            }
        }
    }

    /**
     * 从字典中获取选项
     *
     * @param dictType 字典类型
     * @return 选项数组
     */
    private String[] getDictOptions(String dictType) {
        DictionaryServiceExtProvider bindDictService = ContextHelper.getBean(DictionaryServiceExtProvider.class);
        if (bindDictService == null) {
            throw new BusinessException(Status.FAIL_SERVICE_UNAVAILABLE, "DictionaryService未实现，@ExcelOption无法关联字典！");
        }
        String[] options = bindDictService.getKeyValueList(dictType).stream().map(KeyValue::getK).toArray(String[]::new);
        if (V.isEmpty(options)) {
            log.warn(clazz.getSimpleName() + " @ExcelOption 关联字典: " + dictType + " 无值");
        }
        return options;
    }
}
