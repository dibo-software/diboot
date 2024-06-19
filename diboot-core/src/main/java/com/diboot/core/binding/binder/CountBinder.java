/*
 * Copyright (c) 2015-2029, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.core.binding.binder;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.diboot.core.binding.Binder;
import com.diboot.core.binding.annotation.BindCount;
import com.diboot.core.binding.binder.remote.RemoteBindingManager;
import com.diboot.core.binding.helper.ResultAssembler;
import com.diboot.core.config.Cons;
import com.diboot.core.exception.InvalidUsageException;
import com.diboot.core.service.BaseService;
import com.diboot.core.util.MapUtils;
import com.diboot.core.util.V;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 关联子项count计数绑定
 * @author mazc@dibo.ltd
 * @version v2.6.0
 * @date 2022/06/23
 */
public class CountBinder<T> extends EntityListBinder<T> {
    private static final Logger log = LoggerFactory.getLogger(CountBinder.class);

    /***
     * 构造方法
     * @param annotation
     * @param voList
     */
    public CountBinder(BindCount annotation, List voList){
        super(annotation.entity(), voList);
    }

    @Override
    public void bind() {
        if(V.isEmpty(annoObjectList)){
            return;
        }
        if(V.isEmpty(refObjJoinCols)){
            throw new InvalidUsageException("exception.invalidUsage.binder.parseConditionFailed");
        }
        Map<String, Long> valueListCountMap;
        if(middleTable == null){
            this.simplifySelectColumns();
            super.buildQueryWrapperJoinOn();
            // 查询条件为空时不进行查询
            if (queryWrapper.isEmptyOfNormal()) {
                return;
            }
            queryWrapper.groupBy(refObjJoinCols);
            List<Map<String, Object>> countMapList;
            // 查询entity列表: List<Role>
            if(V.isEmpty(this.module)){
                // 本地查询获取匹配结果的entityList
                countMapList = this.getCountMapList(queryWrapper);
            }
            else{
                // 远程调用获取
                countMapList = RemoteBindingManager.fetchMapList(module, remoteBindDTO);
            }
            if(V.notEmpty(countMapList)){
                valueListCountMap = this.buildMatchKey2ListCountMap(countMapList);
                ResultAssembler.bindCountPropValue(annoObjectField, super.getMatchedAnnoObjectList(), getAnnoObjJoinFlds(), valueListCountMap);
            }
        }
        else{
            if(refObjJoinCols.size() > 1){
                throw new InvalidUsageException(NOT_SUPPORT_MSG);
            }
            // 提取注解条件中指定的对应的列表
            Map<String, List> trunkObjCol2ValuesMap = super.buildTrunkObjCol2ValuesMap();
            Map<String, Long> middleTableCountResultMap = middleTable.executeOneToManyCountQuery(trunkObjCol2ValuesMap);
            if(V.isEmpty(middleTableCountResultMap)){
                return;
            }
            valueListCountMap = new HashMap<>();
            for(Map.Entry<String, Long> entry : middleTableCountResultMap.entrySet()){
                // count <roleId>
                Long count = entry.getValue();
                if(V.isEmpty(count)){
                    count = 0L;
                }
                valueListCountMap.put(entry.getKey(), count);
            }
            // 绑定结果
            ResultAssembler.bindCountPropValue(annoObjectField, super.getMatchedAnnoObjectList(), getAnnoObjJoinFlds(), valueListCountMap);
        }
    }

    /**
     * 简化select列，仅select主键
     */
    @Override
    protected void simplifySelectColumns() {
        List<String> selectColumns = new ArrayList<>(8);
        selectColumns.addAll(refObjJoinCols);
        selectColumns.add("count(*) AS "+ Binder.COUNT_COL);
        if(remoteBindDTO != null){
            remoteBindDTO.setSelectColumns(selectColumns);
        }
        this.queryWrapper.select(selectColumns);
    }

    /**
     * 获取EntityList
     * @param queryWrapper
     * @return
     */
    private List<Map<String, Object>> getCountMapList(Wrapper queryWrapper) {
        if(referencedService instanceof BaseService){
            return ((BaseService)referencedService).getMapList(queryWrapper);
        }
        else{
            return referencedService.listMaps(queryWrapper);
        }
    }

    /**
     * 构建匹配key-count目标的map
     * @param mapCountList
     * @return
     */
    private Map<String, Long> buildMatchKey2ListCountMap(List<Map<String, Object>> mapCountList){
        Map<String, Long> key2TargetCountMap = new HashMap<>(mapCountList.size());
        if(V.isEmpty(mapCountList)) {
            return Collections.emptyMap();
        }
        StringBuilder sb = new StringBuilder();
        for(Map<String, Object> countMap : mapCountList) {
            sb.setLength(0);
            for(int i=0; i<refObjJoinCols.size(); i++) {
                Object pkValue = MapUtils.getIgnoreCase(countMap, refObjJoinCols.get(i));
                if(i > 0){
                    sb.append(Cons.SEPARATOR_COMMA);
                }
                sb.append(pkValue);
            }
            // 查找匹配Key
            String matchKey = sb.toString();
            // 获取list
            Long entityCount = (Long) MapUtils.getIgnoreCase(countMap, Binder.COUNT_COL);
            key2TargetCountMap.put(matchKey, entityCount);
        }
        sb.setLength(0);
        return key2TargetCountMap;
    }

}