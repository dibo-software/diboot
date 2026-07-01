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

import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.write.handler.CellWriteHandler;
import cn.idev.excel.write.handler.context.CellWriteHandlerContext;
import cn.idev.excel.write.metadata.style.WriteCellStyle;
import cn.idev.excel.write.metadata.style.WriteFont;
import com.diboot.core.util.AnnotationUtils;
import com.diboot.file.excel.annotation.ExcelColor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;

import java.util.regex.Pattern;

/**
 * Excel写入 单元格颜色 Handler
 *
 * @author wind
 * @version v3.5.0
 */
@Slf4j
public class ColorWriteHandler implements CellWriteHandler {

    /**
     * 设置单元格颜色
     */
    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        WriteCellData<?> cellData = context.getFirstCellData();
        if (cellData == null || cellData.getStringValue() == null) return;
        ExcelColor color = AnnotationUtils.getAnnotation(context.getHeadData().getField(), ExcelColor.class);
        if (color != null) {
            fillColor(cellData, color);
        }
        ExcelColor.List list = AnnotationUtils.getAnnotation(context.getHeadData().getField(), ExcelColor.List.class);
        if (list != null) {
            for (ExcelColor item : list.value()) {
                fillColor(cellData, item);
            }
        }
    }

    /**
     * 填充颜色
     *
     * @param cellData 单元格
     * @param color    颜色
     */
    private void fillColor(WriteCellData<?> cellData, ExcelColor color) {
        if (Pattern.matches(color.regex(), cellData.getStringValue())) {
            WriteCellStyle writeCellStyleData = cellData.getOrCreateStyle();
            if (color.backgroundColor() != IndexedColors.AUTOMATIC && writeCellStyleData.getFillForegroundColor() == null) {
                writeCellStyleData.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
                writeCellStyleData.setFillForegroundColor(color.backgroundColor().getIndex());
            }
            if (color.fontColor() != IndexedColors.AUTOMATIC && writeCellStyleData.getWriteFont() == null) {
                WriteFont writeFont = new WriteFont();
                writeFont.setColor(color.fontColor().getIndex());
                writeCellStyleData.setWriteFont(writeFont);
            }
        }
    }

}
