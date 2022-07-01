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
import com.diboot.core.data.access.DataAccessAnnoCache;
import com.diboot.core.data.access.DataAccessInterface;
import com.diboot.core.exception.InvalidUsageException;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 数据权限控制拦截器
 *
 * @author jerryma@dibo.ltd
 * @version v2.0
 * @date 2020/09/29
 */
public class DataAccessControlInterceptor implements InnerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(DataAccessControlInterceptor.class);

    private final Set<String> noCheckpointCache = new CopyOnWriteArraySet<>();

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        if (noCheckpointCache.contains(ms.getId())) {
            return;
        }
        // 替换SQL
        PluginUtils.MPBoundSql mpBoundSql = PluginUtils.mpBoundSql(boundSql);
        String originSql = mpBoundSql.sql();
        PlainSelect selectBody = parseSelectBody(originSql);
        if (selectBody != null && selectBody.getFromItem() instanceof Table) {
            Table mainTable = (Table) selectBody.getFromItem();
            String tableName = S.removeEsc(mainTable.getName());
            Class<?> entityClass = BindingCacheManager.getEntityClassByTable(tableName);
            // 无权限检查点注解，不处理
            if (entityClass == null || !DataAccessAnnoCache.hasDataAccessCheckpoint(entityClass)) {
                noCheckpointCache.add(ms.getId());
                return;
            }
            appendDataAccessCondition(selectBody, mainTable, entityClass);
            String newSql = selectBody.toString();
            mpBoundSql.sql(newSql);
            // 打印修改后的SQL
            if (log.isTraceEnabled() && V.notEquals(originSql, newSql)) {
                log.trace("DataAccess Interceptor SQL : {}", newSql);
            }
        } else {
            noCheckpointCache.add(ms.getId());
        }
    }

    /**
     * 附加数据访问权限控制SQL条件
     *
     * @param entityClass
     */
    private void appendDataAccessCondition(PlainSelect selectBody, Table mainTable, Class<?> entityClass) {
        Expression dataAccessExpression = buildDataAccessExpression(mainTable, entityClass);
        // 主表需要数据权限检查
        if (dataAccessExpression != null) {
            String whereStmt = selectBody.getWhere() == null ? null : selectBody.getWhere().toString();
            if (selectBody.getWhere() == null) {
                selectBody.setWhere(dataAccessExpression);
            } else {
                AndExpression andExpression = new AndExpression(selectBody.getWhere(), dataAccessExpression);
                selectBody.setWhere(andExpression);
            }
            log.debug("DataAccess Inteceptor Where: {} => {}", whereStmt, selectBody.getWhere().toString());
        }
    }

    /**
     * 构建数据权限检查条件
     *
     * @param mainTable
     * @param entityClass
     * @return
     */
    private Expression buildDataAccessExpression(Table mainTable, Class<?> entityClass) {
        return DataAccessAnnoCache.getDataPermissionMap(entityClass).entrySet().stream().map(entry -> {
            DataAccessInterface checkImpl = ContextHelper.getBean(DataAccessInterface.class);
            if (checkImpl == null) {
                throw new InvalidUsageException("无法从上下文中获取数据权限的接口实现：DataAccessInterface");
            }
            List<? extends Serializable> idValues = checkImpl.getAccessibleIds(entityClass, entry.getKey());
            if (idValues == null) {
                return null;
            }
            String idCol = entry.getValue();
            if (mainTable.getAlias() != null) {
                idCol = mainTable.getAlias().getName() + "." + idCol;
            }
            if (idValues.isEmpty()) {
                return new IsNullExpression().withLeftExpression(new Column(idCol));
            } else if (idValues.size() == 1) {
                EqualsTo equalsTo = new EqualsTo();
                equalsTo.setLeftExpression(new Column(idCol));
                if(idValues.get(0) instanceof Long){
                    equalsTo.setRightExpression(new LongValue((Long)idValues.get(0)));
                }
                else{
                    equalsTo.setRightExpression(new StringValue(S.defaultValueOf(idValues.get(0))));
                }
                return equalsTo;
            } else {
                String conditionExpr = idCol + " IN ";
                if(idValues.get(0) instanceof Long){
                    conditionExpr += "(" + S.join(idValues, ", ") + ")";
                }
                else{
                    conditionExpr += "('" + S.join(idValues, "', '") + "')";
                }
                try {
                    return CCJSqlParserUtil.parseCondExpression(conditionExpr);
                } catch (JSQLParserException e) {
                    log.warn("解析condition异常: " + conditionExpr, e);
                }
            }
            return null;
        }).filter(Objects::nonNull).reduce(AndExpression::new).orElse(null);
    }

    /**
     * 解析SelectBody
     *
     * @param sql
     * @return
     */
    private PlainSelect parseSelectBody(String sql) {
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            if (statement instanceof Select) {
                SelectBody selectBody = ((Select) statement).getSelectBody();
                if (selectBody instanceof PlainSelect) {
                    return (PlainSelect) selectBody;
                }
            }
        } catch (JSQLParserException e) {
            log.warn("解析SQL异常: " + sql, e);
        }
        return null;
    }
}
