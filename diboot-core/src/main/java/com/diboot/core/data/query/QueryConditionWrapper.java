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

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.diboot.core.binding.query.Comparison;
import com.diboot.core.config.Cons;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.IGetter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


/**
 * 查询条件包装类
 * @author JerryMa
 * @version v3.0.0
 * @date 2023/2/15
 * Copyright © diboot.com
 */
@Slf4j
@Accessors(chain = true)
public class QueryConditionWrapper extends QueryCondition implements Serializable {
    private static final long serialVersionUID = -5010290906947799451L;

    public QueryConditionWrapper() {
    }

    public QueryConditionWrapper(Map<String, Object> queryParamMap) {
        super(queryParamMap);
    }

    public <T,R> QueryConditionWrapper selectFields(SFunction<T, R>... fieldGetters) {
        if(selectFields == null) {
            selectFields = new ArrayList<>();
        }
        for(SFunction<T,R> getter : fieldGetters) {
            selectFields.add(BeanUtils.convertSFunctionToFieldName(getter));
        }
        return this;
    }

    public QueryConditionWrapper selectFields(String... fieldNames) {
        super.selectFields(fieldNames);
        return this;
    }

    public <T> QueryConditionWrapper eq(IGetter<T> fieldGetter, Object value) {
        appendCriteria(fieldGetter, Comparison.EQ, value);
        return this;
    }

    public <T> QueryConditionWrapper ge(IGetter<T> fieldGetter, Object value) {
        appendCriteria(fieldGetter, Comparison.GE, value);
        return this;
    }

    public <T> QueryConditionWrapper gt(IGetter<T> fieldGetter, Object value) {
        appendCriteria(fieldGetter, Comparison.GT, value);
        return this;
    }

    public <T> QueryConditionWrapper between(IGetter<T> fieldGetter, Object beginValue, Object endValue) {
        appendCriteria(fieldGetter, Comparison.BETWEEN, Arrays.asList(beginValue, endValue));
        return this;
    }

    public <T> QueryConditionWrapper in(IGetter<T> fieldGetter, Object value) {
        appendCriteria(fieldGetter, Comparison.IN, value);
        return this;
    }

    public <T> QueryConditionWrapper isNull(IGetter<T> fieldGetter) {
        appendCriteria(fieldGetter, Comparison.IS_NULL, null);
        return this;
    }

    public <T> QueryConditionWrapper isNotNull(IGetter<T> fieldGetter) {
        appendCriteria(fieldGetter, Comparison.IS_NOT_NULL, null);
        return this;
    }

    public <T> QueryConditionWrapper le(IGetter<T> fieldGetter, Object value) {
        appendCriteria(fieldGetter, Comparison.LE, value);
        return this;
    }

    public <T> QueryConditionWrapper lt(IGetter<T> fieldGetter, Object value) {
        appendCriteria(fieldGetter, Comparison.LT, value);
        return this;
    }

    public <T> QueryConditionWrapper like(IGetter<T> fieldGetter, Object value) {
        return contains(fieldGetter, value);
    }

    public <T> QueryConditionWrapper contains(IGetter<T> fieldGetter, Object value) {
        appendCriteria(fieldGetter, Comparison.CONTAINS, value);
        return this;
    }

    public <T> QueryConditionWrapper endsWith(IGetter<T> fieldGetter, Object value) {
        appendCriteria(fieldGetter, Comparison.ENDSWITH, value);
        return this;
    }

    public <T> QueryConditionWrapper startsWith(IGetter<T> fieldGetter, Object value) {
        appendCriteria(fieldGetter, Comparison.STARTSWITH, value);
        return this;
    }

    public <T> QueryConditionWrapper ne(IGetter<T> fieldGetter, Object value) {
        appendCriteria(fieldGetter, Comparison.NOT_EQ, value);
        return this;
    }

    public <T> QueryConditionWrapper notIn(IGetter<T> fieldGetter, Object value) {
        appendCriteria(fieldGetter, Comparison.NOT_IN, value);
        return this;
    }

    public <T> QueryConditionWrapper orderByDesc(IGetter<T> fieldGetter) {
        if(this.orderItems == null) {
            this.orderItems = new ArrayList<>();
        }
        this.orderItems.add(BeanUtils.convertToFieldName(fieldGetter) + Cons.SEPARATOR_COLON + Cons.ORDER_DESC);
        return this;
    }

    public <T> QueryConditionWrapper orderByAsc(IGetter<T> fieldGetter) {
        if(this.orderItems == null) {
            this.orderItems = new ArrayList<>();
        }
        this.orderItems.add(BeanUtils.convertToFieldName(fieldGetter));
        return this;
    }

    public <T> void appendCriteria(IGetter<T> fieldGetter, Comparison comparison, Object value) {
        if(criteriaList == null) {
            criteriaList = new ArrayList<>();
        }
        criteriaList.add(new CriteriaItem(BeanUtils.convertToFieldName(fieldGetter), comparison, value));
    }

    /**
     * 更新查询条件
     * @param fieldGetter
     * @return
     */
    public <T> QueryConditionWrapper updateCriteria(IGetter<T> fieldGetter, Comparison comparison, Object value) {
        super.updateCriteria(BeanUtils.convertToFieldName(fieldGetter), comparison, value);
        return this;
    }

    /**
     * 移除查询条件
     * @param fieldGetter
     * @return
     */
    public <T> QueryConditionWrapper removeCriteria(IGetter<T> fieldGetter) {
        super.removeCriteria(BeanUtils.convertToFieldName(fieldGetter));
        return this;
    }

}