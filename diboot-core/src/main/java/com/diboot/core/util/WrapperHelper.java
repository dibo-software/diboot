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
package com.diboot.core.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.config.Cons;

import java.util.function.Function;

/**
 * Wrapper帮助类
 *
 * @author wind
 * @version v2.5.0
 * @date 2022/01/05
 */
public class WrapperHelper {

    /**
     * 构建排序
     *
     * @param query        查询器
     * @param orderBy      排序条件
     * @param field2column 字段转列名函数
     */
    public static void buildOrderBy(QueryWrapper<?> query, String orderBy, Function<String, String> field2column) {
        // 解析排序
        if (V.notEmpty(orderBy)) {
            for (String field : S.split(orderBy, Cons.SEPARATOR_COMMA)) {
                V.securityCheck(field);
                String[] fieldAndOrder = field.split(Cons.SEPARATOR_COLON);
                String columnName = field2column.apply(fieldAndOrder[0]);
                if (fieldAndOrder.length > 1 && Cons.ORDER_DESC.equalsIgnoreCase(fieldAndOrder[1])) {
                    query.orderByDesc(columnName);
                } else {
                    query.orderByAsc(columnName);
                }
            }
        }
    }
}
