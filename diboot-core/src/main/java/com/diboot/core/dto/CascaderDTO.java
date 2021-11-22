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
package com.diboot.core.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 级联DTO
 *
 * @author : uu
 * @version : v2.4.0
 * @Date 2021/11/22  14:20
 */
@Getter
@Setter
public class CascaderDTO implements Serializable {
    private static final long serialVersionUID = -3659986749020101170L;

    /**
     * 级联触发值
     */
    private Object triggerValue;

    /**
     * 级联目标数据配置(entity名称)
     */
    @NotNull(message = "级联目标不能为空")
    private List<String> targetList;

    /**
     * 扩展参数
     */
    private Map<String, Object> extParams;
}
