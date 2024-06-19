/*
 * Copyright (c) 2015-2022, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.core.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 排序参数 DTO
 *
 * @author wind
 * @version v3.0.0
 * @date 2022/07/20
 */
@Getter
@Setter
@Accessors(chain = true)
public class SortParamDTO<ID extends Serializable> implements Serializable {
    private static final long serialVersionUID = 30303L;

    /**
     * 操作对象ID
     */
    @NotNull(message = "{validation.id.NotNull.message}")
    private ID id;

    /**
     * 新序号
     */
    @NotNull(message = "{validation.newSortId.NotNull.message}")
    private Long newSortId;

    /**
     * 旧序号
     */
    private Long oldSortId;

    /**
     * 新的父节点ID（未改化传原父节点ID）
     * <p>
     * Tree 结构数据：应指定 parentIdField，及传递 newParentId；当跨层级时无需传递 oldSortId
     */
    private ID newParentId;

}
