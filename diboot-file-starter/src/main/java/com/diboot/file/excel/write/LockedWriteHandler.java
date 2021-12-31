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

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.handler.context.SheetWriteHandlerContext;
import com.diboot.core.util.D;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * Sheet保护密码写入处理器
 *
 * @author wind
 * @version v2.4.0
 * @date 2021/11/15
 */
public class LockedWriteHandler implements SheetWriteHandler {

    /**
     * 密码
     */
    private final String password ;

    public LockedWriteHandler() {
        this.password = D.today();
    }

    public LockedWriteHandler(String password) {
        this.password = password;
    }

    /**
     * 设置密码
     */
    @Override
    public void afterSheetCreate(SheetWriteHandlerContext context) {
        Sheet sheet = context.getWriteSheetHolder().getSheet();
        sheet.protectSheet(password);
    }

}
