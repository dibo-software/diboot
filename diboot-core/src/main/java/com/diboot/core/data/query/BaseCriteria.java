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
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Collection;


/**
 * 基础查询条件
 * @author JerryMa
 * @version v3.2.0
 * @date 2024/1/15
 * Copyright © diboot.com
 */
@Getter @Setter @Accessors(chain = true)
public class BaseCriteria implements Serializable {
    private static final long serialVersionUID = 2012502786391342220L;

    protected String field;

    protected String comparison = Comparison.EQ.name();

    protected Object value;

    public BaseCriteria() {
    }

    public BaseCriteria(String field, Object value) {
        this.field = field;
        if(value instanceof Collection || (value != null && value.getClass().isArray())) {
            this.comparison = Comparison.IN.name();
        }
        this.value = value;
    }

    public BaseCriteria(String field, Comparison comparison, Object value) {
        this.field = field;
        this.comparison = comparison.name();
        this.value = value;
    }

    public BaseCriteria update(Comparison comparison, Object value) {
        this.comparison = comparison.name();
        this.value = value;
        return this;
    }

    public Comparison getComparison() {
        return Comparison.valueOf(comparison);
    }

    public String getComparisonName() {
        return comparison;
    }
}