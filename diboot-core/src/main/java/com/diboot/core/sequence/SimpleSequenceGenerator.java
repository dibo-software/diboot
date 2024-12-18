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
package com.diboot.core.sequence;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.diboot.core.binding.parser.EntityInfoCache;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.D;
import com.diboot.core.util.S;
import com.diboot.core.util.SqlExecutor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

import java.lang.invoke.SerializedLambda;
import java.util.List;
import java.util.Map;

/**
 * 简单的序列生成
 *
 * @author wind
 * @version v3.5.1
 * @date 2024/12/18
 */
@Setter
@Accessors(chain = true)
public class SimpleSequenceGenerator extends SequenceGenerator {

    /**
     * 表名
     */
    private final String table;
    /**
     * 列名
     */
    private final String column;
    /**
     * 前缀
     */
    private String prefix = "No.";
    /**
     * 日期格式
     */
    private String dateFormat = "yyMMdd";
    /**
     * 流水号长度
     */
    private int serialNumberLength = 3;

    @SneakyThrows
    public <T> SimpleSequenceGenerator(ICounter counter, SFunction<T, ?> entityGetter) {
        super(counter);

        SerializedLambda serializedLambda = BeanUtils.getSerializedLambda(entityGetter);

        String methodName = serializedLambda.getImplMethodName();
        if (!methodName.startsWith("is") && !methodName.startsWith("get")) {
            throw new RuntimeException("get方法名称: " + methodName + ", 不符合java bean规范");
        }
        // get方法开头为 is 或者 get，将方法名 去除is或者get，然后首字母小写，就是属性名
        int prefixLen = methodName.startsWith("is") ? 2 : 3;
        String fieldName = S.uncapFirst(methodName.substring(prefixLen));

        Class<?> entityClass = Class.forName(serializedLambda.getImplClass().replaceAll("/", "."));
        EntityInfoCache entityInfo = new EntityInfoCache(entityClass, null);

        this.table = entityInfo.getTableName();
        this.column = entityInfo.getPropInfo().getColumnByField(fieldName);
    }

    public SimpleSequenceGenerator(ICounter counter, String table, String column) {
        super(counter);
        this.table = table;
        this.column = column;
    }

    @Override
    @SneakyThrows
    protected long getInitValue() {
        // SELECT MAX(code) AS max FROM table WHERE create_time > 'yyyy-MM-dd'
        String select = "SELECT MAX(" + column + ") AS max FROM " + table;
        String date = getDate();
        List<Map<String, Object>> list = SqlExecutor.executeQuery(date == null ? select : select + " WHERE create_time > '" + date + "'");
        long value = 0L;
        if (list != null && !list.isEmpty() && list.get(0) != null && list.get(0).get("max") != null) {
            int beginIndex = dateFormat.length() + prefix.length();
            value = Long.parseLong(list.get(0).get("max").toString().substring(beginIndex, beginIndex + serialNumberLength));
        }
        return value;
    }

    @Override
    public Object buildFillValue(Map<String, Object> formData) {
        return String.format(prefix + "%s%0" + serialNumberLength + "d", D.now(dateFormat), incrementAndGet());
    }

}
