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
package com.diboot.core.binding.parser;

import com.diboot.core.binding.query.Comparison;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 字段
 * @author JerryMa
 * @version v2.6.0
 * @date 2022/6/2
 * Copyright © diboot.com
 */
@Getter @Setter @Accessors(chain = true)
public class FieldComparison implements Serializable {
    private static final long serialVersionUID = -1080962768714815036L;

    private String fieldName;

    private Comparison comparison;

    private Object value;

    public FieldComparison(){}

    public FieldComparison(String fieldName, Comparison comparison, Object value) {
        this.fieldName = fieldName;
        this.comparison = comparison;
        this.value = value;
    }

}