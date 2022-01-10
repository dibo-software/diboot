/*
 * Copyright (c) 2015-2021, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.core.binding.binder.remote;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 远程绑定DTO定义
 *
 * @author JerryMa
 * @version v2.4.0
 * @date 2021/11/2
 * Copyright © diboot.com
 */
@Getter @Setter @Accessors(chain = true)
public class RemoteBindDTO implements Serializable {
    private static final long serialVersionUID = -3339006060332345228L;

    private String entityClassName;
    private String[] selectColumns;
    private String refJoinCol;
    private Collection<?> inConditionValues;
    private List<String> additionalConditions;
    private String orderBy;
    private String resultType;

    public RemoteBindDTO() {
    }

    public RemoteBindDTO(Class<?> entityClass) {
        this.entityClassName = entityClass.getName();
    }

}
