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
 * 比较条件枚举类
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/08/06
 */
public enum Comparison {
    EQ, // 相等，默认
    IN, // IN

    STARTSWITH, //以xx起始
    ENDSWITH, //以xx结尾
    LIKE, // LIKE
    CONTAINS, //包含，等同LIKE

    GT, // 大于
    GE, // 大于等于
    LT, // 小于
    LE, // 小于等于

    BETWEEN, //介于-之间
    BETWEEN_BEGIN, //介于之后
    BETWEEN_END, //介于之前

    NOT_EQ,  //不等于
    NOT_IN // 不在...内
}
