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
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.handler.context.SheetWriteHandlerContext;
import com.alibaba.excel.write.metadata.holder.WriteHolder;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Map;

/**
 * Excel 冻结窗口
 *
 * @author wind
 * @version v2.4.0
 * @date 2021/11/16
 */
public class FreezePaneWriteHandler implements SheetWriteHandler {

    /**
     * 拆分的水平位置
     */
    private Integer colSplit;
    /**
     * 拆分的垂直位置
     */
    private Integer rowSplit;
    /**
     * 在右窗格中可见的左列
     */
    private Integer leftmostColumn;
    /**
     * 在底部窗格中可见的顶行
     */
    private Integer topRow;

    /**
     * 动态冻结表头
     */
    public FreezePaneWriteHandler() {
    }

    /**
     * 创建拆分（freezepane）。 任何现有的冻结窗格或拆分窗格都将被覆盖。
     * 如果 colSplit 和 rowSplit 都为零，则删除现有的冻结窗格
     */
    public FreezePaneWriteHandler(int colSplit, int rowSplit) {
        this.colSplit = colSplit;
        this.rowSplit = rowSplit;
    }

    public FreezePaneWriteHandler(int colSplit, int rowSplit, int leftmostColumn, int topRow) {
        this(colSplit, rowSplit);
        this.leftmostColumn = leftmostColumn;
        this.topRow = topRow;
    }

    @Override
    public void afterSheetCreate(SheetWriteHandlerContext context) {
        Sheet sheet = context.getWriteSheetHolder().getSheet();
        if (this.colSplit == null) {
            WriteHolder writeHolder = context.getWriteContext().currentWriteHolder();
            Map<Integer, Head> headMap = writeHolder.excelWriteHeadProperty().getHeadMap();
            sheet.createFreezePane(0, headMap.get(0).getHeadNameList().size());
        } else if (this.leftmostColumn == null) {
            sheet.createFreezePane(this.colSplit, this.rowSplit);
        } else {
            sheet.createFreezePane(this.colSplit, this.rowSplit, this.leftmostColumn, this.topRow);
        }
    }

}
