/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.core.binding.query;

/**
 * 查询策略（针对空值等的查询处理策略）
 * @author JerryMa
 * @version v2.2.1
 * @date 2021/5/7
 * Copyright © diboot.com
 */
public enum Strategy {
    /**
     * 忽略空字符串""
     */
    IGNORE_EMPTY,
    /**
     * 空字符串""参与查询
     */
    INCLUDE_EMPTY,
    /**
     * null参与构建isNull
     */
    INCLUDE_NULL,
}
