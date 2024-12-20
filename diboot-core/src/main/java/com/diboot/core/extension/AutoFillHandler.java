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
package com.diboot.core.extension;

import com.diboot.core.entity.AbstractEntity;
import com.diboot.core.vo.LabelValue;

import java.util.Map;

/**
 * 自动赋值字段值 处理类
 * @author JerryMa
 * @version v3.6.0
 * @date 2024/12/10
 */
public interface AutoFillHandler {

    /**
     * 处理器定义
     * @return
     */
    default LabelValue definition() {
        return new LabelValue(this.getClass().getSimpleName(), this.getClass().getSimpleName());
    }

    /**
     * 是否需要更新，默认只新建不更新
     * @return
     */
    default boolean requireUpdate() {
        return false;
    }

    /**
     * 构建填充值（传入entity对象）
     * @param entityData
     * @return
     * @param <T>
     */
    default <T extends AbstractEntity> Object buildFillValue(T entityData) {
        return buildFillValue(entityData.toMap());
    }

    /**
     * 构建填充值（传入map对象）
     * @param entityDataMap
     * @return
     */
    Object buildFillValue(Map<String, Object> entityDataMap);

}