/*
 * Copyright (c) 2015-2099, www.dibo.ltd (service@dibo.ltd).
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

import cn.idev.excel.write.handler.CellWriteHandler;
import cn.idev.excel.write.handler.WorkbookWriteHandler;
import cn.idev.excel.write.handler.WriteHandler;
import cn.idev.excel.write.handler.context.CellWriteHandlerContext;
import cn.idev.excel.write.metadata.holder.WriteSheetHolder;
import cn.idev.excel.write.metadata.holder.WriteWorkbookHolder;
import com.diboot.core.util.AnnotationUtils;
import com.diboot.core.util.V;
import com.diboot.file.excel.annotation.ExcelMerge;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.*;

/**
 * Excel写入 单元合并 Handler
 *
 * @author wind
 * @version v3.5.0
 */
@Slf4j
public class MergeWriteHandler implements WorkbookWriteHandler, CellWriteHandler {

    private final Set<Integer> columnIndexSet = new HashSet<>();

    private Integer headerRows;

    /**
     * 记录合并列与表头行数
     */
    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        if (headerRows == null && context.getRelativeRowIndex() > 0)
            headerRows = context.getRowIndex() - context.getRelativeRowIndex();
        if (context.getRowIndex() != 0) return;
        ExcelMerge merge = AnnotationUtils.getAnnotation(context.getHeadData().getField(), ExcelMerge.class);
        if (merge != null) columnIndexSet.add(context.getColumnIndex());
    }

    /**
     * 数据填充完之后进行内容扫描合并
     */
    @Override
    public void afterWorkbookDispose(WriteWorkbookHolder writeWorkbookHolder) {
        for (WriteSheetHolder sheet : writeWorkbookHolder.getHasBeenInitializedSheetNameMap().values()) {
            for (WriteHandler handler : sheet.getWriteHandlerList()) {
                if (handler instanceof MergeWriteHandler) {
                    ((MergeWriteHandler) handler).afterSheetDispose(sheet);
                }
            }
        }
    }

    /**
     * 对比单元格内容进行合并
     */
    public void afterSheetDispose(WriteSheetHolder writeSheetHolder){
        if (columnIndexSet.isEmpty() || headerRows == null) return;
        Sheet sheet = writeSheetHolder.getSheet();
        Map<Integer, Integer> map = new HashMap<>();
        Row preRow = sheet.getRow(headerRows);
        int totalRowNum = sheet.getLastRowNum() + headerRows;
        for (int rowNum = headerRows + 1; rowNum < totalRowNum; rowNum++) {
            Row row = sheet.getRow(rowNum);
            for (Integer index : columnIndexSet) {
                if (equalsValue(preRow.getCell(index), row.getCell(index))) {
                    if (!map.containsKey(index)) map.put(index, preRow.getRowNum());
                } else if (map.containsKey(index)) {
                    sheet.addMergedRegionUnsafe(new CellRangeAddress(map.remove(index), preRow.getRowNum(), index, index));
                }
            }
            preRow = row;
        }
        int endRowNum = preRow.getRowNum();
        map.forEach((key, value) -> sheet.addMergedRegionUnsafe(new CellRangeAddress(value, endRowNum, key, key)));
    }

    /**
     * 单元格值是否一样
     *
     * @param source 原单元格
     * @param target 目标单元格
     * @return 值是否一样
     */
    private boolean equalsValue(Cell source, Cell target) {
        if (source == null || target == null || source.getCellType() != target.getCellType()) return false;
        return switch (source.getCellType()) {
            case STRING -> V.equals(source.getStringCellValue(), target.getStringCellValue());
            case NUMERIC -> V.equals(source.getNumericCellValue(), target.getNumericCellValue());
            case BOOLEAN -> V.equals(source.getBooleanCellValue(), target.getBooleanCellValue());
            default -> false;
        };
    }
}
