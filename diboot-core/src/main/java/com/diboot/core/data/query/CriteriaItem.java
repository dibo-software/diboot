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
 * 查询条件条目
 * @author JerryMa
 * @version v3.0.0
 * @date 2023/2/15
 * Copyright © diboot.com
 */
@Getter @Setter @Accessors(chain = true)
public class CriteriaItem implements Serializable {
    private static final long serialVersionUID = -2342876399137671211L;

    private String joinTable;

    private String onLink;

    private String onWhere;

    private String field;

    private String comparison = Comparison.EQ.name();

    private Object value;

    public CriteriaItem() {
    }

    public CriteriaItem(String field, Object value) {
        this.field = field;
        if(value != null && value instanceof Collection) {
            this.comparison = Comparison.IN.name();
        }
        this.value = value;
    }

    public CriteriaItem(String field, Comparison comparison, Object value) {
        this.field = field;
        this.comparison = comparison.name();
        this.value = value;
    }

    public CriteriaItem update(Comparison comparison, Object value) {
        this.comparison = comparison.name();
        this.value = value;
        return this;
    }

    public CriteriaItem joinOn(String joinTable, String onLink, String onWhere) {
        this.joinTable = joinTable;
        this.onLink = onLink;
        this.onWhere = onWhere;
        return this;
    }

}