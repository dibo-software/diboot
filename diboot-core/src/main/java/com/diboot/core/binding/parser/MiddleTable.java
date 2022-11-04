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
package com.diboot.core.binding.parser;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.diboot.core.binding.binder.BaseBinder;
import com.diboot.core.binding.cache.BindingCacheManager;
import com.diboot.core.binding.helper.ResultAssembler;
import com.diboot.core.config.BaseConfig;
import com.diboot.core.config.Cons;
import com.diboot.core.exception.InvalidUsageException;
import com.diboot.core.util.S;
import com.diboot.core.util.SqlExecutor;
import com.diboot.core.util.V;
import org.apache.ibatis.jdbc.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 中间表
 * @author mazc@dibo.ltd<br>
 * @version 2.0<br>
 * @date 2019/04/01 <br>
 */
public class MiddleTable {
    private static final Logger log = LoggerFactory.getLogger(MiddleTable.class);
    /**
     * 中间表
     */
    private String table;
    /**
     * 主干对象列映射
     */
    private Map<String, String> trunkObjColMapping;
    /**
     * 枝干对象列映射
     */
    private Map<String, String> branchObjColMapping;
    /**
     * 附加条件
     */
    private List<String> additionalConditions;

    public MiddleTable(String table){
        this.table = table;
    }

    /**
     * 与主对象的关联
     * @param middleTableCol
     * @param trunkObjCol
     * @return
     */
    public MiddleTable connectTrunkObj(String middleTableCol, String trunkObjCol){
        if(trunkObjColMapping == null){
            trunkObjColMapping = new LinkedHashMap<>(8);
        }
        trunkObjColMapping.put(trunkObjCol, middleTableCol);
        return this;
    }

    /**
     * 与枝对象的关联
     * @param middleTableCol
     * @param branchObjCol
     * @return
     */
    public MiddleTable connectBranchObj(String middleTableCol, String branchObjCol){
        if(branchObjColMapping == null){
            branchObjColMapping = new LinkedHashMap<>(8);
        }
        else if(branchObjColMapping.size() >= 1){
            throw new InvalidUsageException(BaseBinder.NOT_SUPPORT_MSG);
        }
        branchObjColMapping.put(middleTableCol, branchObjCol);
        return this;
    }

    /**
     * 添加中间表查询所需的附加条件
     * @param additionalCondition
     */
    public MiddleTable addAdditionalCondition(String additionalCondition) {
        if(this.additionalConditions == null){
            this.additionalConditions = new ArrayList<>();
        }
        this.additionalConditions.add(additionalCondition);
        return this;
    }

    public String getTable(){
        return this.table;
    }

    public Map<String, String> getTrunkObjColMapping(){
        return this.trunkObjColMapping;
    }

    public Map<String, String> getBranchObjColMapping(){
        return this.branchObjColMapping;
    }

    /**
     * 执行1-1关联查询，得到关联映射Map
     * @param trunkObjCol2ValuesMap
     * @return
     */
    public Map<String, Object> executeOneToOneQuery(Map<String, List> trunkObjCol2ValuesMap){
        if(V.isEmpty(trunkObjCol2ValuesMap)){
            log.warn("不合理的中间表查询：无过滤条件！");
            return Collections.emptyMap();
        }
        //id //org_id
        EntityInfoCache linkage = BindingCacheManager.getEntityInfoByTable(table);
        // 有定义mapper，首选mapper
        if(linkage != null){
            List<Map<String, Object>> resultSetMapList = queryByMapper(linkage, trunkObjCol2ValuesMap);
            return ResultAssembler.convertToOneToOneResult(resultSetMapList, trunkObjColMapping, branchObjColMapping);
        }
        else{
            // 提取中间表查询SQL: SELECT id, org_id FROM department WHERE id IN(?)
            List paramValueList = new ArrayList();
            String sql = toSQL(trunkObjCol2ValuesMap, paramValueList);
            // 执行查询并合并结果
            try {
                List<Map<String, Object>> resultSetMapList = SqlExecutor.executeQuery(sql, paramValueList);
                return ResultAssembler.convertToOneToOneResult(resultSetMapList, trunkObjColMapping, branchObjColMapping);
            }
            catch (Exception e) {
                log.error("中间表查询异常: " + sql, e);
                return Collections.emptyMap();
            }
        }
    }

    /**
     * 执行1-N关联查询，得到关联映射Map
     * @param trunkObjCol2ValuesMap
     * @return
     */
    public Map<String, List> executeOneToManyQuery(Map<String, List> trunkObjCol2ValuesMap){
        if(V.isEmpty(trunkObjCol2ValuesMap)){
            throw new InvalidUsageException("不合理的中间表查询：无过滤条件！");
        }
        //user_id //role_id
        EntityInfoCache linkage = BindingCacheManager.getEntityInfoByTable(table);
        // 有定义mapper，首选mapper
        if(linkage != null){
            List<Map<String, Object>> resultSetMapList = queryByMapper(linkage, trunkObjCol2ValuesMap);
            return ResultAssembler.convertToOneToManyResult(resultSetMapList, trunkObjColMapping, branchObjColMapping);
        }
        else{
            // 提取中间表查询SQL: SELECT user_id, role_id FROM user_role WHERE user_id IN(?)
            List paramValueList = new ArrayList();
            String sql = toSQL(trunkObjCol2ValuesMap, paramValueList);
            // 执行查询并合并结果
            try {
                List<Map<String, Object>> resultSetMapList = SqlExecutor.executeQuery(sql, paramValueList);
                return ResultAssembler.convertToOneToManyResult(resultSetMapList, trunkObjColMapping, branchObjColMapping);
            }
            catch (Exception e) {
                log.error("中间表查询异常: " + sql, e);
                return Collections.emptyMap();
            }
        }
    }

    /**
     * 通过定义的Mapper查询结果
     * @param linkage
     * @param trunkObjCol2ValuesMap
     * @return
     */
    private List<Map<String, Object>> queryByMapper(EntityInfoCache linkage, Map<String, List> trunkObjCol2ValuesMap){
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntityClass(linkage.getEntityClass());
        // select所需字段
        queryWrapper.select(getSelectColumns());
        for(Map.Entry<String, List> entry : trunkObjCol2ValuesMap.entrySet()){
            String column = entry.getKey();
            if(column != null && V.notEmpty(entry.getValue())){
                queryWrapper.in(column, entry.getValue());
            }
        }
        if(additionalConditions != null){
            for(String condition : additionalConditions){
                queryWrapper.apply(condition);
            }
        }
        BaseMapper mapper = linkage.getBaseMapper();
        List<Map<String, Object>> resultSetMapList = mapper.selectMaps(queryWrapper);
        return resultSetMapList;
    }

    /**
     * 转换查询SQL
     * @param trunkObjCol2ValuesMap 注解外键值的列表，用于拼接SQL查询
     * @return
     */
    private String toSQL(Map<String, List> trunkObjCol2ValuesMap, List paramValueList){
        if(V.isEmpty(trunkObjCol2ValuesMap)){
            return null;
        }
        // select所需字段
        String selectColumns = S.join(getSelectColumns());
        // 构建SQL
        return new SQL(){{
            SELECT(selectColumns);
            FROM(table);
            for(Map.Entry<String, List> entry : trunkObjCol2ValuesMap.entrySet()){
                String column = entry.getKey();
                if(column != null && V.notEmpty(entry.getValue())){
                    List values = (List)entry.getValue().stream().distinct().collect(Collectors.toList());
                    String params = S.repeat("?", ",", values.size());
                    WHERE(column + " IN (" + params + ")");
                    paramValueList.addAll(values);
                }
            }
            // 添加删除标记
            boolean appendDeleteFlag = true;
            if(additionalConditions != null){
                for(String condition : additionalConditions){
                    WHERE(condition);
                    if(S.containsIgnoreCase(condition, Cons.COLUMN_IS_DELETED)){
                        appendDeleteFlag = false;
                    }
                }
            }
            // 如果需要删除
            if(appendDeleteFlag){
                String deletedCol = ParserCache.getDeletedColumn(table);
                if(deletedCol != null){
                    WHERE(deletedCol + " = " + BaseConfig.getActiveFlagValue());
                }
            }
        }}.toString();
    }

    private String[] getSelectColumns(){
        List<String> columns = new ArrayList<>(8);
        // select所需字段
        if(V.notEmpty(trunkObjColMapping)){
            for(Map.Entry<String, String> entry : trunkObjColMapping.entrySet()){
                columns.add(entry.getValue());
            }
        }
        if(V.notEmpty(branchObjColMapping)){
            for(Map.Entry<String, String> entry : branchObjColMapping.entrySet()){
                columns.add(entry.getKey());
            }
        }
        return S.toStringArray(columns);
    }

}
