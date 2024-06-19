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
package com.diboot.core.entity;

import com.diboot.core.config.Cons;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 树形结构实体父类
 * @author mazc@dibo.ltd
 * @version v3.0
 * @date 2022/10/12
 */
@Getter
@Setter
@Accessors(chain = true)
public class BaseTreeEntity<T extends Serializable> extends BaseEntity<T> {
    private static final long serialVersionUID = 10205L;

    /**
     * 父级ID
     */
    private T parentId;

    public BaseTreeEntity<T> setParentId(T parentId) {
        this.parentId = parentId;
        return this;
    }

    public T getParentId() {
        return this.parentId;
    }

    /**
     * 父级ID的全路径
     * <p>
     * 格式：/([0-9a-f]+,)+/g
     */
    @JsonIgnore
    private String parentIdsPath;

    public BaseTreeEntity<T> setParentIdsPath(String parentIdsPath) {
        if (V.notEmpty(parentIdsPath) && !S.endsWith(parentIdsPath, Cons.SEPARATOR_COMMA)) {
            parentIdsPath += Cons.SEPARATOR_COMMA;
        }
        this.parentIdsPath = parentIdsPath;
        return this;
    }
}
