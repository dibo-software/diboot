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
package com.diboot.core.vo;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * LabelValue键值对形式的VO（用于构建显示名Name-存储值Value形式的结果）
 *
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/1/4
 */
@Getter
@Setter
@Accessors(chain = true)
public class LabelValue implements Serializable {
    private static final long serialVersionUID = -2358161241655186720L;

    public LabelValue() {
    }

    public LabelValue(String label, Object value) {
        this.value = value;
        this.label = label;
    }

    /**
     * 对象类型
     */
    private String type;

    /**
     * label: 显示值
     */
    private String label;

    /**
     * value: 存储值
     */
    private Object value;

    /**
     * 扩展值
     */
    private Object ext;

    /**
     * 是否为叶子节点
     */
    private Boolean leaf;

    /**
     * 是否禁用；非异步加载时，非叶子节点且无叶子节点时会自动禁用该节点
     */
    private Boolean disabled;

    /**
     * 子节点集合
     */
    private List<LabelValue> children;

    @JsonGetter("isLeaf")
    public Boolean isLeaf() {
        return leaf;
    }

}
