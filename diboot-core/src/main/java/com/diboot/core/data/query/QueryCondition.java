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
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.diboot.core.binding.cache.BindingCacheManager;
import com.diboot.core.binding.parser.EntityInfoCache;
import com.diboot.core.binding.query.Comparison;
import com.diboot.core.config.Cons;
import com.diboot.core.util.BeanUtils;
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
    protected List<CriteriaItem> criteriaList;

    @Getter
    @Setter
    protected Pagination pagination;

    @Getter
    protected List<String> orderItems;

    @Getter
    @Setter
    protected List<String> selectFields;

    @Getter
    @Setter
    protected List<String> excludeFields;

    protected Map<String, Object> queryParamMap;

    public QueryCondition() {
    }

    public QueryCondition(Map<String, Object> queryParamMap) {
        initQueryParamMap(queryParamMap, false);
    }

    public QueryCondition(Map<String, Object> queryParamMap, boolean includeEmpty) {
        initQueryParamMap(queryParamMap, includeEmpty);
    }

    private void initQueryParamMap(Map<String, Object> queryParamMap, boolean includeEmpty) {
        if(V.notEmpty(queryParamMap)) {
            for(Map.Entry<String, Object> entry : queryParamMap.entrySet()) {
                if(Pagination.isPaginationParam(entry.getKey())) {
                    continue;
                }
                if(V.isEmpty(entry.getValue()) && !includeEmpty) {
                    log.debug("忽略空的参数: {}", entry.getKey());
                    continue;
                }
                addCriteria(entry.getKey(), entry.getValue());
            }
        }
    }

    public <T,FT> QueryCondition select(SFunction<T,FT> ... fieldGetters) {
        if(selectFields == null) {
            selectFields = new ArrayList<>();
        }
        for(SFunction<T,FT> getter : fieldGetters) {
            selectFields.add(BeanUtils.convertSFunctionToFieldName(getter));
        }
        return this;
    }

    public QueryCondition select(String... fieldNames) {
        if(selectFields == null) {
            selectFields = new ArrayList<>();
        }
        selectFields.addAll(Arrays.asList(fieldNames));
        return this;
    }

    public <T,FT> QueryCondition eq(SFunction<T,FT> fieldGetter, Object value) {
        if(value != null && value instanceof Collection) {
            return in(fieldGetter, value);
        }
        appendCriteria(fieldGetter, Comparison.EQ, value);
        return this;
    }

    public QueryCondition eq(String fieldName, Object value) {
        if(value != null && value instanceof Collection) {
            return in(fieldName, value);
        }
        appendCriteria(fieldName, Comparison.EQ, value);
        return this;
    }

    public <T,FT> QueryCondition ge(SFunction<T,FT> fieldGetter, Object value) {
        appendCriteria(fieldGetter, Comparison.GE, value);
        return this;
    }

    public QueryCondition ge(String fieldName, Object value) {
        appendCriteria(fieldName, Comparison.GE, value);
        return this;
    }

    public <T,FT> QueryCondition gt(SFunction<T,FT> fieldGetter, Object value) {
        appendCriteria(fieldGetter, Comparison.GT, value);
        return this;
    }

    public QueryCondition gt(String fieldName, Object value) {
        appendCriteria(fieldName, Comparison.GT, value);
        return this;
    }

    public <T,FT> QueryCondition between(SFunction<T,FT> fieldGetter, Object beginValue, Object endValue) {
        appendCriteria(fieldGetter, Comparison.BETWEEN, Arrays.asList(beginValue, endValue));
        return this;
    }

    public QueryCondition between(String fieldName, Object value) {
        appendCriteria(fieldName, Comparison.BETWEEN, value);
        return this;
    }

    public <T,FT> QueryCondition in(SFunction<T,FT> fieldGetter, Object value) {
        appendCriteria(fieldGetter, Comparison.IN, value);
        return this;
    }

    public QueryCondition in(String fieldName, Object value) {
        appendCriteria(fieldName, Comparison.IN, value);
        return this;
    }

    public <T,FT> QueryCondition isNull(SFunction<T,FT> fieldGetter) {
        appendCriteria(fieldGetter, Comparison.IS_NULL, null);
        return this;
    }

    public QueryCondition isNull(String fieldName, Object value) {
        appendCriteria(fieldName, Comparison.IS_NULL, value);
        return this;
    }

    public <T,FT> QueryCondition isNotNull(SFunction<T,FT> fieldGetter) {
        appendCriteria(fieldGetter, Comparison.IS_NOT_NULL, null);
        return this;
    }

    public QueryCondition isNotNull(String fieldName, Object value) {
        appendCriteria(fieldName, Comparison.IS_NOT_NULL, value);
        return this;
    }

    public <T,FT> QueryCondition le(SFunction<T,FT> fieldGetter, Object value) {
        appendCriteria(fieldGetter, Comparison.LE, value);
        return this;
    }

    public QueryCondition le(String fieldName, Object value) {
        appendCriteria(fieldName, Comparison.LE, value);
        return this;
    }

    public <T,FT> QueryCondition lt(SFunction<T,FT> fieldGetter, Object value) {
        appendCriteria(fieldGetter, Comparison.LT, value);
        return this;
    }

    public QueryCondition lt(String fieldName, Object value) {
        appendCriteria(fieldName, Comparison.LT, value);
        return this;
    }

    public <T,FT> QueryCondition like(SFunction<T,FT> fieldGetter, Object value) {
        return contains(fieldGetter, value);
    }

    public QueryCondition like(String fieldName, Object value) {
        return contains(fieldName, value);
    }

    public <T,FT> QueryCondition contains(SFunction<T,FT> fieldGetter, Object value) {
        appendCriteria(fieldGetter, Comparison.CONTAINS, value);
        return this;
    }

    public QueryCondition contains(String fieldName, Object value) {
        appendCriteria(fieldName, Comparison.CONTAINS, value);
        return this;
    }

    public <T,FT> QueryCondition endsWith(SFunction<T,FT> fieldGetter, Object value) {
        appendCriteria(fieldGetter, Comparison.ENDSWITH, value);
        return this;
    }

    public QueryCondition endsWith(String fieldName, Object value) {
        appendCriteria(fieldName, Comparison.ENDSWITH, value);
        return this;
    }

    public <T,FT> QueryCondition startsWith(SFunction<T,FT> fieldGetter, Object value) {
        appendCriteria(fieldGetter, Comparison.STARTSWITH, value);
        return this;
    }

    public QueryCondition startsWith(String fieldName, Object value) {
        appendCriteria(fieldName, Comparison.STARTSWITH, value);
        return this;
    }

    public <T,FT> QueryCondition ne(SFunction<T,FT> fieldGetter, Object value) {
        appendCriteria(fieldGetter, Comparison.NOT_EQ, value);
        return this;
    }

    public QueryCondition ne(String fieldName, Object value) {
        appendCriteria(fieldName, Comparison.NOT_EQ, value);
        return this;
    }

    public <T,FT> QueryCondition notIn(SFunction<T,FT> fieldGetter, Object value) {
        appendCriteria(fieldGetter, Comparison.NOT_IN, value);
        return this;
    }

    public QueryCondition notIn(String fieldName, Object value) {
        appendCriteria(fieldName, Comparison.NOT_IN, value);
        return this;
    }

    public <T,FT> QueryCondition orderByDesc(SFunction<T,FT> fieldGetter) {
        return this.orderByDesc(BeanUtils.convertSFunctionToFieldName(fieldGetter));
    }

    public <T> QueryCondition orderByDesc(String fieldName) {
        if(this.orderItems == null) {
            this.orderItems = new ArrayList<>();
        }
        this.orderItems.add(fieldName + Cons.SEPARATOR_COLON + Cons.ORDER_DESC);
        return this;
    }

    public <T,FT> QueryCondition orderByAsc(SFunction<T,FT> fieldGetter) {
        return this.orderByAsc(BeanUtils.convertSFunctionToFieldName(fieldGetter));
    }

    public <T> QueryCondition orderByAsc(String fieldName) {
        if(this.orderItems == null) {
            this.orderItems = new ArrayList<>();
        }
        this.orderItems.add(fieldName);
        return this;
    }

    public QueryCondition orderBy(String fieldName) {
        if(this.orderItems == null) {
            this.orderItems = new ArrayList<>();
        }
        this.orderItems.add(fieldName);
        return this;
    }

    public <T,FT> QueryCondition appendCriteria(SFunction<T,FT> fieldGetter, Comparison comparison, Object value) {
        return appendCriteria(BeanUtils.convertSFunctionToFieldName(fieldGetter), comparison, value);
    }

    public <T,FT> QueryCondition appendCriteria(String fieldName, Comparison comparison, Object value) {
        if(criteriaList == null) {
            criteriaList = new ArrayList<>();
        }
        criteriaList.add(new CriteriaItem(fieldName, comparison, value));
        return this;
    }

    /**
     * 更新查询条件
     * @param fieldGetter
     * @return
     */
    public <T,FT> QueryCondition updateCriteria(SFunction<T,FT> fieldGetter, Comparison comparison, Object value) {
        this.updateCriteria(BeanUtils.convertSFunctionToFieldName(fieldGetter), comparison, value);
        return this;
    }

    /**
     * 移除查询条件
     * @param fieldGetter
     * @return
     */
    public <T,FT> QueryCondition removeCriteria(SFunction<T,FT> fieldGetter) {
        this.removeCriteria(BeanUtils.convertSFunctionToFieldName(fieldGetter));
        return this;
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
     * 添加 BaseCriteria 简单查询条件
     * @param criteria
     * @return
     */
    public QueryCondition addCriteria(BaseCriteria criteria) {
        if(criteriaList == null) {
            criteriaList = new ArrayList<>();
        }
        criteriaList.add(new CriteriaItem(criteria));
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
     * 是否包含某字段的条件
     * @param field
     * @return
     */
    public boolean containsCriteria(String field) {
        if(V.isEmpty(this.criteriaList)) {
            return false;
        }
        for(CriteriaItem item : criteriaList) {
            if(item.getField().equals(field)) {
                return true;
            }
        }
        return false;
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
        for(CriteriaItem item : criteriaList) {
            if(item.getField().equals(field)) {
                return item;
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
                this.orderItems = Collections.singletonList(Pagination.ORDER_BY_ID_DESC);
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
    public <T> QueryWrapper<T> toQueryWrapper(Class<T> entityClass) {
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
                    case IS_NULL:
                        query.isNull(field);
                        break;
                    case IS_NOT_NULL:
                        query.isNotNull(field);
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

    /**
     * 是否为默认排序
     * @return
     */
    public boolean isDefaultOrder() {
        if (V.notEmpty(orderItems) && orderItems.size() == 1 && orderItems.get(0).equals(Pagination.ORDER_BY_ID_DESC)) {
            return true;
        }
        return false;
    }
}
