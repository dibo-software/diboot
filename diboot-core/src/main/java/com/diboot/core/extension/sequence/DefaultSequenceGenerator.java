/*
 * Copyright (c) 2015-2099, www.dibo.ltd (service@dibo.ltd).
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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.IService;
import com.diboot.core.binding.cache.BindingCacheManager;
import com.diboot.core.binding.parser.EntityInfoCache;
import com.diboot.core.extension.sequence.counter.SeqCounter;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.apache.ibatis.reflection.property.PropertyNamer;

import java.lang.invoke.SerializedLambda;
import java.util.List;
import java.util.Map;

/**
 * 默认的流程序列生成器
 *
 * @author wind
 * @version v3.5.1
 * @date 2024/12/18
 */
@Setter
@Accessors(chain = true)
public class DefaultSequenceGenerator<T> extends SequenceGenerator {

    /**
     * 实体类型
     */
    private Class<T> entityClass;
    /**
     * 列名
     */
    private String fieldName;
    /**
     * 序列号组成部分
     */
    private List<Part> parts;

    @SneakyThrows
    public DefaultSequenceGenerator(SeqCounter counter, SFunction<T, ?> entityGetter, List<Part> sequenceParts) {
        super(counter);
        SerializedLambda lambda = BeanUtils.getSerializedLambda(entityGetter);
        this.fieldName = PropertyNamer.methodToProperty(lambda.getImplMethodName());
        this.entityClass = (Class<T>)Class.forName(lambda.getImplClass().replaceAll("/", "."));
        this.parts = sequenceParts;
    }

    @Override
    @SneakyThrows
    protected synchronized long getInitValue() {
        EntityInfoCache entityInfo = BindingCacheManager.getEntityInfoByClass(entityClass);
        IService<T> entityService = entityInfo.getService();
        QueryWrapper<T> queryWrapper = new QueryWrapper<T>().select(entityInfo.getPropInfo().getColumnByField(fieldName))
                .orderByDesc(entityInfo.getPropInfo().getIdColumn()); //如果id非有序，可换成 create_time
        T entity = entityService.getOne(queryWrapper, false);
        if(entity == null) {
            return 0;
        }
        // 获取这个字段中最新/最大编号
        String fieldLatestValue = (String)BeanUtils.getProperty(entity, fieldName);
        if(V.isEmpty(fieldLatestValue)){
            return 0;
        }
        String datePartFormat = null;
        String currentDateVal = "";
        int dateStartIndex = 0;
        for(Part part : parts) {
            if("date".equals(part.getType())) {
                datePartFormat = part.getValue();
                currentDateVal = PartGenerator.generate(part);
                break;
            }
            dateStartIndex += part.getLength();
        }
        int sequenceStartIndex = 0;
        for(Part part : parts) {
            if("seq".equals(part.getType())) {
                String beginSeqValue = S.substring(fieldLatestValue, sequenceStartIndex, sequenceStartIndex+part.getLength());
                long initIndex = Long.parseLong(beginSeqValue);
                if (datePartFormat != null) {
                    String latestDateValue = S.substring(fieldLatestValue, dateStartIndex, dateStartIndex+datePartFormat.length());
                    if(!latestDateValue.equals(currentDateVal)) {
                        initIndex = 0;
                    }
                }
                return initIndex;
            }
            sequenceStartIndex += part.getLength();;
        }
        return 0;
    }

    @Override
    public Object buildFillValue(Map<String, Object> formData) {
        StringBuilder sb = new StringBuilder();
        for(Part part : parts) {
            if("field".equals(part.getType())) {
                sb.append(formData.get(part.getValue()));
            }
            else if("seq".equals(part.getType())) {
                String expression = "%0" + part.getLength() + "d";
                sb.append(String.format(expression, incrementAndGet()));
            }
            else {
                sb.append(PartGenerator.generate(part));
            }
        }
        return sb.toString();
    }

    protected String getCurrentDate() {
        String currentDateVal = null;
        for(Part part : parts) {
            if("date".equals(part.getType())) {
                currentDateVal = PartGenerator.generate(part);
                break;
            }
        }
        return currentDateVal;
    }

}
