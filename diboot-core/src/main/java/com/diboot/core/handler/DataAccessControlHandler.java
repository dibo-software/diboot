/*
 * Copyright (c) 2015-2023, www.dibo.ltd (service@dibo.ltd).
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

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import com.diboot.core.data.access.DataAccessAnnoCache;
import com.diboot.core.data.access.DataScopeManager;
import com.diboot.core.entity.AbstractEntity;
import com.diboot.core.exception.InvalidUsageException;
import com.diboot.core.holder.ThreadLocalHolder;
import com.diboot.core.mapper.DynamicQueryMapper;
import com.diboot.core.util.ContextHolder;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 数据访问控制处理器
 *
 * @author wind
 * @version v2.9
 * @date 2023/03/29
 */
public class DataAccessControlHandler implements MultiDataPermissionHandler {
    private static final Logger log = LoggerFactory.getLogger(DataAccessControlHandler.class);

    private final Set<String> noCheckpointCache = new CopyOnWriteArraySet<>();
    /**
     * 获取全部自定义数据权限拦截器
     */
    private Map<Class<?>, DataScopeManager> entityClassToPermissionMap = null;

    /**
     * 获取数据权限拦截器的实现类
     * @param entityClass
     * @return
     */
    public synchronized DataScopeManager getDataScopeManager(Class<?> entityClass) {
        if(entityClassToPermissionMap == null) {
            entityClassToPermissionMap = new LinkedHashMap<>();
            List<DataScopeManager> dataProtectionHandlers = ContextHolder.getBeans(DataScopeManager.class);
            if(V.notEmpty(dataProtectionHandlers)) {
                for (DataScopeManager protectionHandler : dataProtectionHandlers) {
                    List<Class<?>> entityClasses = protectionHandler.getEntityClasses();
                    if(entityClasses == null) {
                        entityClassToPermissionMap.put(AbstractEntity.class, protectionHandler);
                    }
                    else if(V.notEmpty(entityClasses)) {
                        for (Class<?> entityCls : entityClasses) {
                            entityClassToPermissionMap.put(entityCls, protectionHandler);
                        }
                    }
                }
            }
        }
        DataScopeManager dataScopeManager = entityClassToPermissionMap.get(entityClass);
        if(dataScopeManager == null) {
            dataScopeManager = entityClassToPermissionMap.get(AbstractEntity.class);
            if(dataScopeManager != null) {
                log.debug("获取到全局默认的数据范围控制实现 {}: {}", entityClass.getSimpleName(), dataScopeManager.getClass().getSimpleName());
            }
        }
        else {
            log.debug("获取到 {} 类的数据范围控制实现: {}", entityClass.getSimpleName(), dataScopeManager.getClass().getSimpleName());
        }
        return dataScopeManager;
    }

    @Override
    public Expression getSqlSegment(Table table, Expression where, String mappedStatementId) {
        if (noCheckpointCache.contains(mappedStatementId)) {
            return null;
        }
        // 如果忽略此来源
        if (ThreadLocalHolder.ignoreInterceptor()) {
            return null;
        }
        TableInfo tableInfo = TableInfoHelper.getTableInfo(S.removeEsc(table.getName()));
        // 无权限检查点注解，不处理
        if (tableInfo == null || tableInfo.getEntityType() == null || !DataAccessAnnoCache.hasDataAccessCheckpoint(tableInfo.getEntityType())) {
            if (!S.substringBeforeLast(mappedStatementId, ".").equals(DynamicQueryMapper.class.getName()))
                noCheckpointCache.add(mappedStatementId);
            return null;
        }
        return buildDataAccessExpression(table, tableInfo.getEntityType());
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
            DataScopeManager checkImpl = getDataScopeManager(entityClass);
            if (checkImpl == null) {
                log.warn("未获取到 {} 类的数据范围控制实现，请检查DataScopeManager实现类是否正确实例化！", entityClass.getSimpleName());
                throw new InvalidUsageException("exception.invalidUsage.dataAccessControlHandler.buildDataAccessExpression.message");
            }
            List<? extends Serializable> idValues = checkImpl.getAccessibleIds(entry.getKey());
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
                if (idValues.get(0) instanceof Long) {
                    equalsTo.setRightExpression(new LongValue((Long) idValues.get(0)));
                } else {
                    equalsTo.setRightExpression(new StringValue(S.defaultValueOf(idValues.get(0))));
                }
                return equalsTo;
            } else {
                String conditionExpr = idCol + " IN ";
                if (idValues.get(0) instanceof Long) {
                    conditionExpr += "(" + S.join(idValues, ", ") + ")";
                } else {
                    conditionExpr += "('" + S.join(idValues, "', '") + "')";
                }
                try {
                    return CCJSqlParserUtil.parseCondExpression(conditionExpr);
                } catch (JSQLParserException e) {
                    log.warn("解析condition异常: {}", conditionExpr, e);
                }
            }
            return null;
        }).filter(Objects::nonNull).reduce(AndExpression::new).orElse(null);
    }

}
