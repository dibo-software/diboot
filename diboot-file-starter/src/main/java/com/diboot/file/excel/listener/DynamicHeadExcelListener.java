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
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.metadata.property.ExcelReadHeadProperty;
import com.diboot.file.excel.TableHead;
import com.diboot.file.util.ExcelHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/***
 * 非固定表头的excel数据读取listener
 * @auther wangyl@dibo.ltd
 * @date 2019-10-9
 */
@Slf4j
public abstract class DynamicHeadExcelListener extends AnalysisEventListener<Map<Integer, String>> {
    // 表头
    @Getter
    Map<Integer, String> headMap = new HashMap<>();
    /**
     * 是否为预览模式
     */
    @Setter
    protected boolean preview = false;
    /**
     * 注入request
     */
    @Setter
    private Map<String, Object> requestParams;

    /**
     * 属性名映射
     */
    @Getter
    private final HashMap<Integer, String> fieldNameMap = new HashMap<>();
    /**
     * 列名映射
     */
    @Getter
    private final TreeMap<Integer, List<String>> headNameMap = new TreeMap<>();

    /**
     * 预览数据
     */
    @Getter @Setter
    private List<Map<Integer, String>> previewDataList;

    @Getter
    private Integer totalCount = 0;

    /**
     * 错误信息
     */
    @Getter
    private List<String> errorMsgs;
    @Getter
    protected Integer errorCount = 0;

    // 全部数据
    private List<Map<Integer, String>> dataList = new ArrayList<>();

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        dataList.add(data);
    }


    /**
     * excel表头数据
     **/
    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        this.headMap.clear();
        fieldNameMap.clear();
        headNameMap.clear();
        ExcelReadHeadProperty excelReadHeadProperty = context.currentReadHolder().excelReadHeadProperty();
        for (Map.Entry<Integer, Head> entry : excelReadHeadProperty.getHeadMap().entrySet()) {
            Integer index = entry.getKey();
            Head head = entry.getValue();
            String fieldName = head.getFieldName();
            List<String> headNameList = head.getHeadNameList();
            String name = headNameList.get(headNameList.size() - 1);
            this.headMap.put(index, name);
            fieldNameMap.put(index, fieldName);
            headNameMap.put(index, headNameList);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 自定义校验
        additionalValidate(dataList, requestParams);
        // TODO 检查
        this.totalCount = dataList.size();
        this.previewDataList = totalCount > 100? dataList.subList(0, 100) : dataList;
        if(!preview) {
            saveData(this.headMap, this.dataList, requestParams);
        }
    }

    /**
     * 表头
     * @param headMap
     * @param context
     */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        this.headMap = headMap;
    }

    /**
     * 返回表头
     * @return
     */
    public Map<Integer, String> getHeadMap(){
        return this.headMap;
    }

    /**
     * 获取Excel表头
     *
     * @return 表头映射
     */
    public List<TableHead> getTableHeads() {
        return ExcelHelper.buildTableHeads(headNameMap, fieldNameMap);
    }

    /**
     * 返回结果
     * @return
     */
    public List<Map<Integer, String>> getDataList(){
        return dataList;
    }

    /**
     * <h3>自定义数据检验方式</h3>
     * 例：数据重复性校验等，添加校验批注信息
     */
    protected void additionalValidate(List<Map<Integer, String>> dataList, Map<String, Object> requestParams) {
    }

    /**
     * 保存数据
     */
    protected abstract void saveData(Map<Integer, String> headMap, List<Map<Integer, String>> dataList, Map<String, Object> requestParams);

}
