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
package com.diboot.file.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.diboot.core.util.V;
import com.diboot.file.excel.BaseExcelModel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * excel数据导入导出listener基类
 *
 * @author wangyl@dibo.ltd
 * @date 2019-10-9
 */
@Slf4j
public abstract class FixedHeadExcelListener<T extends BaseExcelModel> extends ReadExcelListener<T> {

    /**
     * 解析后的数据实体list
     */
    @Getter
    private final List<T> dataList = new ArrayList<>();

    /**
     * 所有数据解析成功后的操作
     **/
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (V.isEmpty(dataList)) {
            return;
        }
        super.handle(dataList);
        super.finish();
    }

    @Override
    protected void cachedData(T data) {
        dataList.add(data);
    }

    /**
     * 重新定义数据判断（兼容老版本）
     *
     * @param data 数据
     * @return
     */
    @Override
    protected boolean isProper(T data) {
        return V.isEmpty(data.getComment()) && V.isEmpty(data.getValidateError());
    }

    /**
     * 异常数据拦截（兼容老版本）
     *
     * @param dataList
     */
    @Override
    protected void errorData(List<T> dataList) {
        dataList.stream().filter(data -> V.notEmpty(data.getValidateError()))
                .forEach(data -> addExceptionMsg("第 " + (data.getRowIndex() + 1) + " 行：" + data.getValidateError()));
        super.errorData(dataList);
    }
}
