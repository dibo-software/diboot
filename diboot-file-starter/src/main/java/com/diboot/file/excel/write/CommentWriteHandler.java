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
import com.diboot.core.util.AnnotationUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.file.excel.BaseExcelModel;
import com.diboot.file.excel.annotation.ExcelComment;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import java.util.List;

/**
 * 批注写入处理器
 *
 * @author wind
 * @version v2.4.0
 * @date 2021/11/12
 */
public class CommentWriteHandler implements CellWriteHandler {

    /**
     * 数据集；用于获取数据批注
     */
    private List<? extends BaseExcelModel> dataList;

    public CommentWriteHandler() {
    }

    public CommentWriteHandler(List<? extends BaseExcelModel> dataList) {
        this.dataList = dataList;
    }

    public CommentWriteHandler setDataList(List<? extends BaseExcelModel> dataList) {
        this.dataList = dataList;
        return this;
    }

    /**
     * 头部批注处理
     */
    @Override
    public void afterCellCreate(CellWriteHandlerContext context) {
        Head head = context.getHeadData();
        List<String> headNames = head.getHeadNameList();
        int lastHeadIndex = headNames.size() - 1;
        Integer rowIndex = context.getRowIndex();
        if (!context.getHead() || rowIndex != lastHeadIndex && !headNames.get(rowIndex).equals(headNames.get(lastHeadIndex))) {
            return;
        }
        ExcelComment comment = AnnotationUtils.getAnnotation(head.getField(), ExcelComment.class);
        if (comment == null) {
            return;
        }
        Sheet sheet = context.getWriteSheetHolder().getSheet();
        context.getCell().setCellComment(buildComment(sheet.createDrawingPatriarch(),
                context.getColumnIndex(), sheet.getLastRowNum(), comment.value()));
    }

    /**
     * 数据批注处理
     */
    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        Integer relativeRowIndex = context.getRelativeRowIndex();
        if (context.getHead() || V.isEmpty(dataList) || dataList.size() <= relativeRowIndex) {
            return;
        }
        List<String> comments = dataList.get(relativeRowIndex).getComment().get(context.getHeadData().getFieldName());
        if (V.isEmpty(comments)) {
            return;
        }
        Sheet sheet = context.getWriteSheetHolder().getSheet();
        context.getCell().setCellComment(buildComment(sheet.createDrawingPatriarch(),
                context.getColumnIndex(), sheet.getLastRowNum(), S.join(comments, ";\n")));
    }

    /**
     * 构建批注
     */
    protected Comment buildComment(Drawing<?> drawingPatriarch, int col, int row, String commentContent) {
        Comment comment = drawingPatriarch.createCellComment(new XSSFClientAnchor(0, 0, 0, 0, col, row, col, row));
        comment.setString(new XSSFRichTextString(commentContent));
        return comment;
    }
}
