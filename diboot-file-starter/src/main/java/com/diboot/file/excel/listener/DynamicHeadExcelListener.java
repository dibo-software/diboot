package com.diboot.file.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/***
 * 非固定表头的excel数据读取listener
 * @auther wangyl@dibo.ltd
 * @date 2019-10-9
 */
@Slf4j
public abstract class DynamicHeadExcelListener extends AnalysisEventListener<Map<Integer, String>> {
    // 表头
    Map<Integer, String> headMap = null;
    // 全部数据
    private List<Map<Integer, String>> dataList = new ArrayList<>();

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        dataList.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData(this.headMap, this.dataList);
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
     * 返回结果
     * @return
     */
    public List<Map<Integer, String>> getDataList(){
        return dataList;
    }

    /**
     * 保存数据
     */
    protected abstract void saveData(Map<Integer, String> headMap, List<Map<Integer, String>> dataList);

}
