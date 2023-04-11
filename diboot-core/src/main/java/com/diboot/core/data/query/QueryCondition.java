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
package com.diboot.core.data.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.binding.cache.BindingCacheManager;
import com.diboot.core.binding.parser.EntityInfoCache;
import com.diboot.core.binding.query.Comparison;
import com.diboot.core.config.Cons;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.Pagination;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 查询条件
 * @author JerryMa
 * @version v3.0.0
 * @date 2023/2/15
 * Copyright © diboot.com
 */
@Slf4j
@Accessors(chain = true)
public class QueryCondition implements Serializable {
    private static final long serialVersionUID = -7495538662136985338L;

    @Getter
    private List<CriteriaItem> criteriaList;

    @Getter
    @Setter
    private Pagination pagination;

    @Getter
    private List<String> orderItems;

    @Getter
    @Setter
    private List<String> selectFields;

    @Getter
    @Setter
    private List<String> excludeFields;

    private Map<String, Object> queryParamMap;

    public QueryCondition() {
    }

    public QueryCondition(Map<String, Object> queryParamMap) {
        if(V.notEmpty(queryParamMap)) {
            for(Map.Entry<String, Object> entry : queryParamMap.entrySet()) {
                if(Pagination.isPaginationParam(entry.getKey())) {
                    continue;
                }
                addCriteria(new CriteriaItem(entry.getKey(), Comparison.EQ, entry.getValue()));
            }
        }
    }

    public QueryCondition addCriteria(CriteriaItem... criteriaItems) {
        if(criteriaList == null) {
            criteriaList = new ArrayList<>();
        }
        criteriaList.addAll(Arrays.asList(criteriaItems));
        return this;
    }

    public QueryCondition addCriteria(String field, Object value) {
        if(criteriaList == null) {
            criteriaList = new ArrayList<>();
        }
        criteriaList.add(new CriteriaItem(field, value));
        return this;
    }

    public QueryCondition addCriteria(String field, Comparison comparison, Object value) {
        if(criteriaList == null) {
            criteriaList = new ArrayList<>();
        }
        criteriaList.add(new CriteriaItem(field, comparison, value));
        return this;
    }

    /**
     * 更新查询条件
     * @param field
     * @return
     */
    public QueryCondition updateCriteria(String field, Comparison comparison, Object value) {
        if(criteriaList != null) {
            for(CriteriaItem item : criteriaList) {
                if(item.getField().equals(field)) {
                    item.setComparison(comparison.name()).setValue(value);
                    return this;
                }
            }
        }
        return this;
    }

    /**
     * 移除查询条件
     * @param field
     * @return
     */
    public QueryCondition removeCriteria(String field) {
        if(criteriaList != null) {
            for(CriteriaItem item : criteriaList) {
                if(item.getField().equals(field)) {
                    criteriaList.remove(item);
                    return this;
                }
            }
        }
        return this;
    }

    public QueryCondition selectFields(String... fieldNames) {
        if(selectFields == null) {
            selectFields = new ArrayList<>();
        }
        selectFields.addAll(Arrays.asList(fieldNames));
        return this;
    }

    /**
     * 获取查询参数值
     * @param field
     * @return
     */
    public Object getQueryParamVal(String field) {
        if(V.isEmpty(this.criteriaList)) {
            return null;
        }
        if(this.queryParamMap == null) {
            this.queryParamMap = new HashMap<>();
            this.criteriaList.forEach(c-> this.queryParamMap.put(c.getField(), c.getValue()));
        }
        return this.queryParamMap.get(field);
    }

    /**
     * 获取查询条件
     * @param field
     * @return
     */
    public CriteriaItem getCriteriaItem(String field) {
        if(V.isEmpty(this.criteriaList)) {
            return null;
        }
        if(criteriaList != null) {
            for(CriteriaItem item : criteriaList) {
                if(item.getField().equals(field)) {
                    return item;
                }
            }
        }
        return null;
    }

    public QueryCondition clear() {
        this.criteriaList = null;
        this.orderItems = null;
        this.queryParamMap = null;
        return this;
    }

    public QueryCondition orderBy(String orderItem) {
        this.orderItems = Arrays.asList(orderItem);
        return this;
    }

    public QueryCondition orderBy(List<String> orderItems) {
        this.orderItems = orderItems;
        return this;
    }

    public QueryCondition withPagination(Pagination pagination) {
        this.pagination = pagination;
        if(this.pagination == null) {
            this.pagination = new Pagination();
        }
        else {
            String orderByStr = this.pagination.getOrderBy();
            if(V.notEmpty(orderByStr)) {
                this.orderItems = S.splitToList(orderByStr, Cons.SEPARATOR_COMMA);
            }
            else {
                this.orderItems = Arrays.asList(Cons.FieldName.id.name()+":DESC");
            }
        }
        return this;
    }

    /**
     * 构建 QueryWrapper
     *
     * @param entityClass 实体类型
     * @param <T>
     * @return
     */
    public <T> QueryWrapper<T> buildQueryWrapper(Class<T> entityClass) {
        EntityInfoCache entityInfo = BindingCacheManager.getEntityInfoByClass(entityClass);
        QueryWrapper<T> query = Wrappers.query();
        // 指定查询字段
        if (V.notEmpty(selectFields)) {
            query.select(selectFields.stream().map(entityInfo::getColumnByField).collect(Collectors.toList()));
        }
        // 解析条件
        if (V.notEmpty(criteriaList)) {
            for (CriteriaItem criteriaItem : criteriaList) {
                String field = entityInfo.getColumnByField(criteriaItem.getField());
                Object value = criteriaItem.getValue();
                switch (criteriaItem.getComparison()) {
                    // 相等，默认
                    case EQ:
                        query.eq(field, value);
                        break;
                    // IN
                    case IN:
                        if (value instanceof Collection) {
                            query.in(field, (Collection<?>) value);
                        } else if (value.getClass().isArray()) {
                            query.in(field, (Object[]) value);
                        } else {
                            query.eq(field, value);
                        }
                        break;
                    //以xx起始
                    case STARTSWITH:
                        query.likeRight(field, value);
                        break;
                    //以xx结尾
                    case ENDSWITH:
                        query.likeLeft(field, value);
                        break;
                    // LIKE
                    case LIKE:
                        //包含，等同LIKE
                    case CONTAINS:
                        query.like(field, value);
                        break;
                    // 大于
                    case GT:
                        query.gt(field, value);
                        break;
                    // 大于等于
                    case GE:
                        query.ge(field, value);
                        break;
                    // 小于
                    case LT:
                        query.lt(field, value);
                        break;
                    // 小于等于
                    case LE:
                        query.le(field, value);
                        break;
                    // 介于-之间
                    case BETWEEN:
                        break;
                    // 介于之后
                    case BETWEEN_BEGIN:
                        break;
                    // 介于之前
                    case BETWEEN_END:
                        break;
                    //不等于
                    case NOT_EQ:
                        query.ne(field, value);
                        break;
                    // 不在...内
                    case NOT_IN:
                        if (value instanceof Collection) {
                            query.notIn(field, (Collection<?>) value);
                        } else if (value.getClass().isArray()) {
                            query.notIn(field, (Object[]) value);
                        } else {
                            query.ne(field, value);
                        }
                        break;
                    default:
                        //
                }
            }
        }
        // 解析排序
        if (V.notEmpty(orderItems)) {
            for (String field : orderItems) {
                V.securityCheck(field);
                if (field.contains(":")) {
                    String[] fieldAndOrder = S.split(field, ":");
                    String columnName = entityInfo.getColumnByField(fieldAndOrder[0]);
                    if (Cons.ORDER_DESC.equalsIgnoreCase(fieldAndOrder[1])) {
                        query.orderByDesc(columnName);
                    } else {
                        query.orderByAsc(columnName);
                    }
                } else {
                    query.orderByAsc(S.toSnakeCase(field));
                }
            }
        }
        return query;
    }

}
