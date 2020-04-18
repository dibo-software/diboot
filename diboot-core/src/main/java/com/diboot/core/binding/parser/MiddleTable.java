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

import com.diboot.core.config.BaseConfig;
import com.diboot.core.config.Cons;
import com.diboot.core.util.S;
import com.diboot.core.util.SqlExecutor;
import com.diboot.core.util.V;
import org.apache.ibatis.jdbc.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
     * 与注解VO的外键关联的连接字段
     */
    private String equalsToAnnoObjectFKColumn;
    /**
     * 与被引用Entity属性主键的连接字段
     */
    private String equalsToRefEntityPkColumn;
    /**
     * 附加条件
     */
    private List<String> additionalConditions;

    public MiddleTable(String table){
        this.table = table;
    }

    /**
     * 连接（左手连接VO的外键，右手连接Entity属性的主键）
     * @param equalsToAnnoObjectFKColumn
     * @param equalsToRefEntityPkColumn
     * @return
     */
    public MiddleTable connect(String equalsToAnnoObjectFKColumn, String equalsToRefEntityPkColumn) {
        if(V.notEmpty(this.equalsToAnnoObjectFKColumn) && V.notEquals(this.equalsToAnnoObjectFKColumn, equalsToAnnoObjectFKColumn)){
            log.warn("中间表关联字段被覆盖: {}->{}，暂仅支持单字段关联，请检查注解条件！", this.equalsToAnnoObjectFKColumn, equalsToAnnoObjectFKColumn);
        }
        if(V.notEmpty(this.equalsToRefEntityPkColumn) && V.notEquals(this.equalsToRefEntityPkColumn, equalsToRefEntityPkColumn)){
            log.warn("中间表关联字段被覆盖: {}->{}，暂仅支持单字段关联，请检查注解条件！", this.equalsToRefEntityPkColumn, equalsToRefEntityPkColumn);
        }
        this.equalsToAnnoObjectFKColumn = equalsToAnnoObjectFKColumn;
        this.equalsToRefEntityPkColumn = equalsToRefEntityPkColumn;
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

    public String getEqualsToAnnoObjectFKColumn() {
        return equalsToAnnoObjectFKColumn;
    }

    public String getEqualsToRefEntityPkColumn() {
        return equalsToRefEntityPkColumn;
    }

    /**
     * 执行1-1关联查询，得到关联映射Map
     * @param annoObjectForeignKeyList
     * @return
     */
    public Map<String, Object> executeOneToOneQuery(List annoObjectForeignKeyList){
        // 提取中间表查询SQL: SELECT id, org_id FROM department WHERE id IN(?)
        String sql = toSQL(annoObjectForeignKeyList);
        // 执行查询并合并结果
        //id
        String keyName = getEqualsToAnnoObjectFKColumn(),
        //org_id
        valueName = getEqualsToRefEntityPkColumn();
        Map<String, Object> middleTableResultMap = SqlExecutor.executeQueryAndMergeOneToOneResult(sql, annoObjectForeignKeyList, keyName, valueName);
        return middleTableResultMap;
    }

    /**
     * 执行1-N关联查询，得到关联映射Map
     * @param annoObjectForeignKeyList
     * @return
     */
    public Map<String, List> executeOneToManyQuery(List annoObjectForeignKeyList){
        // 提取中间表查询SQL: SELECT user_id, role_id FROM user_role WHERE user_id IN(?)
        String sql = toSQL(annoObjectForeignKeyList);
        // 执行查询并合并结果
        //user_id
        String keyName = getEqualsToAnnoObjectFKColumn(),
                //role_id
                valueName = getEqualsToRefEntityPkColumn();
        Map<String, List> middleTableResultMap = SqlExecutor.executeQueryAndMergeOneToManyResult(sql, annoObjectForeignKeyList, keyName, valueName);
        return middleTableResultMap;
    }

    /**
     * 转换查询SQL
     * @param annoObjectForeignKeyList 注解外键值的列表，用于拼接SQL查询
     * @return
     */
    public String toSQL(List annoObjectForeignKeyList){
        if(V.isEmpty(annoObjectForeignKeyList)){
            return null;
        }
        String params = S.repeat("?", ",", annoObjectForeignKeyList.size());
        // 构建SQL
        return new SQL(){{
            SELECT(equalsToAnnoObjectFKColumn + Cons.SEPARATOR_COMMA + equalsToRefEntityPkColumn);
            FROM(table);
            WHERE(equalsToAnnoObjectFKColumn + " IN (" + params + ")");
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
            if(appendDeleteFlag && ParserCache.hasDeletedColumn(table)){
                WHERE(Cons.COLUMN_IS_DELETED + " = " + BaseConfig.getActiveFlagValue());
            }
        }}.toString();
    }
}
