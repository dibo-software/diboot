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
package com.diboot.core.binding.query.dynamic;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.diboot.core.binding.QueryBuilder;
import com.diboot.core.binding.parser.ParserCache;
import com.diboot.core.config.BaseConfig;
import com.diboot.core.config.Cons;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.SQL;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 动态SQL构建Provider
 * @author Mazc@dibo.ltd
 * @version v2.0
 * @date 2020/04/15
 */
@Slf4j
public class DynamicSqlProvider {

    /**
     * select中的占位列前缀标识
     */
    public static final String PLACEHOLDER_COLUMN_FLAG = "__";

    /**
     * 构建动态SQL
     * @param ew
     * @return
     */
    public String buildSqlForList(QueryWrapper ew){
        return buildDynamicSql(null, ew);
    }

    /**
     * 构建动态SQL
     * @param page 分页参数，用于MP分页插件AOP，不可删除
     * @param ew
     * @return
     */
    public <DTO> String buildSqlForListWithPage(Page<?> page, QueryWrapper<DTO> ew){
        return buildDynamicSql(page, ew);
    }

    /**
     * 构建动态SQL
     * @param page 分页参数，用于MP分页插件AOP，不可删除
     * @param ew
     * @return
     */
    private <DTO> String buildDynamicSql(Page<?> page, QueryWrapper<DTO> ew){
        DynamicJoinQueryWrapper wrapper = (DynamicJoinQueryWrapper)ew;
        return new SQL() {{
            SELECT_DISTINCT(formatSqlSelect(ew.getSqlSelect(), page));
            FROM(wrapper.getEntityTable()+" self");
            //提取字段，根据查询条件中涉及的表，动态join
            List<AnnoJoiner> annoJoinerList = wrapper.getAnnoJoiners();
            if(V.notEmpty(annoJoinerList)){
                Set<String> tempSet = new HashSet<>();
                StringBuilder sb = new StringBuilder();
                for(AnnoJoiner joiner : annoJoinerList){
                    if(V.notEmpty(joiner.getJoin()) && V.notEmpty(joiner.getOnSegment())){
                        if(joiner.getMiddleTable() != null){
                            sb.setLength(0);
                            sb.append(joiner.getMiddleTable()).append(" ").append(joiner.getMiddleTableAlias()).append(" ON ").append(joiner.getMiddleTableOnSegment());
                            String deletedCol = ParserCache.getDeletedColumn(joiner.getMiddleTable());
                            if(deletedCol != null && S.containsIgnoreCase(joiner.getMiddleTable(), " "+deletedCol) == false){
                                sb.append(" AND ").append(joiner.getMiddleTableAlias()).append(".").append(deletedCol).append(" = ").append(BaseConfig.getActiveFlagValue());
                            }
                            String joinSegment = sb.toString();
                            if(!tempSet.contains(joinSegment)){
                                LEFT_OUTER_JOIN(joinSegment);
                                tempSet.add(joinSegment);
                            }
                        }
                        sb.setLength(0);
                        sb.append(joiner.getJoin()).append(" ").append(joiner.getAlias()).append(" ON ").append(joiner.getOnSegment());
                        String deletedCol = ParserCache.getDeletedColumn(joiner.getJoin());
                        if(deletedCol != null && S.containsIgnoreCase(joiner.getOnSegment(), " "+deletedCol) == false){
                            sb.append(" AND ").append(joiner.getAlias()).append(".").append(deletedCol).append(" = ").append(BaseConfig.getActiveFlagValue());
                        }
                        String joinSegment = sb.toString();
                        if(!tempSet.contains(joinSegment)){
                            LEFT_OUTER_JOIN(joinSegment);
                            tempSet.add(joinSegment);
                        }
                    }
                }
                tempSet = null;
            }
            MergeSegments segments = ew.getExpression();
            if(segments != null){
                String normalSql = segments.getNormal().getSqlSegment();
                if(V.notEmpty(normalSql)){
                    WHERE(formatNormalSql(normalSql));
                    // 动态为主表添加is_deleted=0
                    String isDeletedCol = ParserCache.getDeletedColumn(wrapper.getEntityTable());
                    String isDeletedSection = "self."+ isDeletedCol;
                    if(isDeletedCol != null && QueryBuilder.checkHasColumn(segments.getNormal(), isDeletedSection) == false){
                        WHERE(isDeletedSection+ " = " +BaseConfig.getActiveFlagValue());
                    }
                    if(segments.getOrderBy() != null && !segments.getOrderBy().isEmpty()){
                        String orderBySql = segments.getOrderBy().getSqlSegment();
                        int beginIndex = S.indexOfIgnoreCase(orderBySql,"ORDER BY ");
                        if(beginIndex >= 0){
                            orderBySql = S.substring(orderBySql, beginIndex+"ORDER BY ".length());
                            ORDER_BY(orderBySql);
                        }
                    }
                }
                // 存在联表且无where条件，
                else if(V.notEmpty(annoJoinerList)){
                    // 动态为主表添加is_deleted=0
                    String isDeletedCol = ParserCache.getDeletedColumn(wrapper.getEntityTable());
                    String isDeletedSection = "self."+ isDeletedCol;
                    if(isDeletedCol != null && QueryBuilder.checkHasColumn(segments.getNormal(), isDeletedSection) == false){
                        WHERE(isDeletedSection+ " = " +BaseConfig.getActiveFlagValue());
                    }
                }
            }
        }}.toString();
    }

    /**
     * 格式化sql select列语句
     * @param sqlSelect
     * @return
     */
    private String formatSqlSelect(String sqlSelect, Page<?> page){
        Set<String> columnSets = null;
        StringBuilder sb = new StringBuilder();
        if(V.isEmpty(sqlSelect)){
            sb.append("self.*");
        }
        else {
            String[] columns = S.split(sqlSelect);
            for(int i=0; i<columns.length; i++){
                String column = S.removeDuplicateBlank(columns[i]).trim();
                if(i>0){
                    sb.append(Cons.SEPARATOR_COMMA);
                }
                sb.append("self.").append(column);
                if(columnSets == null) {
                    columnSets = new HashSet<>();
                }
                columnSets.add("self."+column);
            }
        }
        if(page != null && page.orders() != null) {
            for(OrderItem orderItem : page.orders()){
                if((V.isEmpty(sqlSelect) && !S.startsWith(orderItem.getColumn(), "self."))
                    || (columnSets != null && !columnSets.contains(orderItem.getColumn()))
                ){
                    sb.append(Cons.SEPARATOR_COMMA).append(orderItem.getColumn()).append(" AS ").append(S.replace(orderItem.getColumn(), ".", "_")).append(PLACEHOLDER_COLUMN_FLAG);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 格式化where条件的sql
     * @param normalSql
     * @return
     */
    private String formatNormalSql(String normalSql){
        if(normalSql.startsWith("(") && normalSql.endsWith(")")){
            return S.substring(normalSql,1,normalSql.length()-1);
        }
        return normalSql;
    }

}
