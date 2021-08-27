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
package com.diboot.core.handler;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.diboot.core.binding.cache.BindingCacheManager;
import com.diboot.core.binding.data.CheckpointType;
import com.diboot.core.binding.data.DataAccessAnnoCache;
import com.diboot.core.binding.data.DataAccessInterface;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

/**
 * 数据权限控制拦截器
 * @author jerryma@dibo.ltd
 * @version v2.0
 * @date 2020/09/29
 */
public class DataAccessControlInteceptor implements InnerInterceptor {
    private static Logger log = LoggerFactory.getLogger(DataAccessControlInteceptor.class);

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        // 未启用数据权限
        if(ContextHelper.getBean(DataAccessInterface.class) == null){
            return;
        }
        // 替换SQL
        PluginUtils.MPBoundSql mpBoundSql = PluginUtils.mpBoundSql(boundSql);
        String originSql = mpBoundSql.sql();
        PlainSelect selectBody = parseSelectBody(originSql);
        if (selectBody.getFromItem() instanceof Table) {
            Table mainTable = (Table) selectBody.getFromItem();
            String tableName = S.removeEsc(mainTable.getName());
            Class<?> entityClass = BindingCacheManager.getEntityClassByTable(tableName);
            // 无权限检查点注解，不处理
            if (entityClass == null || !DataAccessAnnoCache.hasDataAccessCheckpoint(entityClass)) {
                return;
            }
            appendDataAccessCondition(selectBody, mainTable, entityClass);
            String newSql = selectBody.toString();
            mpBoundSql.sql(newSql);
            // 打印修改后的SQL
            if(log.isTraceEnabled() && V.notEquals(originSql, newSql)){
                log.trace("DataAccess Inteceptor SQL : {}", newSql);
            }
        }
    }

    /**
     * 附加数据访问权限控制SQL条件
     * @param entityClass
     * @return
     */
    private void appendDataAccessCondition(PlainSelect selectBody, Table mainTable, Class<?> entityClass){
        Expression dataAccessExpression = buildDataAccessExpression(mainTable, entityClass);
        // 主表需要数据权限检查
        if(dataAccessExpression != null){
            String whereStmt = selectBody.getWhere() == null? null : selectBody.getWhere().toString();
            if (selectBody.getWhere() == null) {
                selectBody.setWhere(dataAccessExpression);
            }
            else {
                AndExpression andExpression = new AndExpression(selectBody.getWhere(), dataAccessExpression);
                selectBody.setWhere(andExpression);
            }
            log.debug("DataAccess Inteceptor Where: {} => {}", whereStmt, selectBody.getWhere().toString());
        }
        if(V.notEmpty(selectBody.getJoins())){
            for (Join join : selectBody.getJoins()) {
                Table joinTable = (Table) join.getRightItem();
                Class<?> joinEntityClass = BindingCacheManager.getEntityClassByTable(joinTable.getName());
                // 无权限检查点注解，不处理
                if (joinEntityClass == null || !DataAccessAnnoCache.hasDataAccessCheckpoint(joinEntityClass)) {
                    continue;
                }
                Expression joinDataAccessExpression = buildDataAccessExpression(joinTable, joinEntityClass);
                // 主表需要数据权限检查
                if(joinDataAccessExpression != null){
                    String joinOnStmt = join.getOnExpression() == null? null : join.getOnExpression().toString();
                    if (join.getOnExpression() == null) {
                        join.setOnExpression(joinDataAccessExpression);
                    }
                    else {
                        AndExpression andExpression = new AndExpression(join.getOnExpression(), joinDataAccessExpression);
                        join.setOnExpression(andExpression);
                    }
                    log.debug("DataAccess Inteceptor Join: {} => {}", joinOnStmt, join.getOnExpression().toString());
                }
            }
        }
    }

    /**
     * 构建数据权限检查条件
     * @param mainTable
     * @param entityClass
     * @return
     */
    private Expression buildDataAccessExpression(Table mainTable, Class<?> entityClass) {
        Expression dataAccessExpression = null;
        DataAccessInterface checkImpl = ContextHelper.getBean(DataAccessInterface.class);
        for(CheckpointType type : CheckpointType.values()){
            String idCol = DataAccessAnnoCache.getDataPermissionColumn(entityClass, type);
            if(V.isEmpty(idCol)){
                continue;
            }
            List<Long> idValues = checkImpl.getAccessibleIds(type);
            if(V.isEmpty(idValues)){
                continue;
            }
            if(idValues.size() == 1){
                EqualsTo equalsTo = new EqualsTo();
                if(mainTable.getAlias() != null){
                    idCol = mainTable.getAlias().getName() + "." + idCol;
                }
                equalsTo.setLeftExpression(new Column(idCol));
                equalsTo.setRightExpression(new LongValue(idValues.get(0)));

                dataAccessExpression = equalsTo;
            }
            else{
                if(mainTable.getAlias() != null){
                    idCol = mainTable.getAlias().getName() + "." + idCol;
                }
                String conditionExpr = idCol + " IN (" + S.join(idValues, ",") + ")";
                Expression valuesExpression = null;
                try{
                    valuesExpression = CCJSqlParserUtil.parseCondExpression(conditionExpr);
                }
                catch (JSQLParserException e){
                    log.warn("解析condition异常: " + conditionExpr, e);
                }
                dataAccessExpression = valuesExpression;
            }
            // 数据权限只有一个
            break;
        }
        return dataAccessExpression;
    }

    /**
     * 解析SelectBody
     * @param sql
     * @return
     */
    private PlainSelect parseSelectBody(String sql){
        Select select = null;
        try{
            select = (Select) CCJSqlParserUtil.parse(sql);
        }
        catch (JSQLParserException e){
            log.warn("解析SQL异常: "+sql, e);
        }
        return select != null? (PlainSelect) select.getSelectBody() : null;
    }
}
