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


/**
 * 查询条件
 * @author JerryMa
 * @version v3.0.0
 * @date 2023/2/15
 * Copyright © diboot.com
 */
@Slf4j
public class QueryCondition implements Serializable {
    private static final long serialVersionUID = -7495538662136985338L;

    @Getter
    private List<CriteriaItem> criteriaList;

    @Getter @Setter @Accessors(chain = true)
    private Pagination pagination;

    @Getter @Setter @Accessors(chain = true)
    private List<String> orderItems;

    @Getter @Setter @Accessors(chain = true)
    private List<String> selectFields;

    @Getter @Setter @Accessors(chain = true)
    private List<String> excludeFields;

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

    public QueryCondition selectFields(String... fieldNames) {
        if(selectFields == null) {
            selectFields = new ArrayList<>();
        }
        selectFields.addAll(Arrays.asList(fieldNames));
        return this;
    }

    public QueryCondition clear() {
        this.criteriaList = null;
        this.orderItems = null;
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

}
