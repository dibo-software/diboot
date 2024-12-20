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
package com.diboot.core.extension.sequence;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.diboot.core.binding.cache.BindingCacheManager;
import com.diboot.core.binding.parser.EntityInfoCache;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.D;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.apache.ibatis.reflection.property.PropertyNamer;

import java.lang.invoke.SerializedLambda;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 简单的序列生成器
 *
 * @author wind
 * @version v3.5.1
 * @date 2024/12/18
 */
@Setter
@Accessors(chain = true)
public class SimpleSequenceGenerator extends SequenceGenerator {

    /**
     * 实体类型
     */
    private final Class<?> entityClass;
    /**
     * 列名
     */
    private final String fieldName;
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
    public <T> SimpleSequenceGenerator(SeqCounter counter, SFunction<T, ?> entityGetter) {
        super(counter);
        SerializedLambda lambda = BeanUtils.getSerializedLambda(entityGetter);
        this.fieldName = PropertyNamer.methodToProperty(lambda.getImplMethodName());
        this.entityClass = Class.forName(lambda.getImplClass().replaceAll("/", "."));
    }

    @Override
    @SneakyThrows
    protected long getInitValue() {
        EntityInfoCache entityInfo = BindingCacheManager.getEntityInfoByClass(entityClass);
        BaseMapper<?> mapper = entityInfo.getBaseMapper();
        // SELECT MAX(code) AS max FROM table WHERE create_time > 'yyyy-MM-dd'
        QueryWrapper<?> queryWrapper = Wrappers.query().select("MAX(" + entityInfo.getPropInfo().getColumnByField(fieldName) + ") AS max");
        LocalDate date = getDate();
        queryWrapper.gt(date != null, "create_time", date);
        List<Map<String, Object>> list = mapper.selectMaps((Wrapper) queryWrapper);
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
