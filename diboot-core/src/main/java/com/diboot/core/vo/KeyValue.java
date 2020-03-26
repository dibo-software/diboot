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

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * KeyValue键值对形式的VO（用于构建显示名Name-存储值Value形式的结果）
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/1/4
 */
@Getter @Setter @Accessors(chain = true)
public class KeyValue implements Serializable {
    private static final long serialVersionUID = -2358161241655186720L;

    public KeyValue(){}

    public KeyValue(String key, Object value){
        this.k = key;
        this.v = value;
    }

    /***
     * key: 显示值，需要显示的name/label文本
     */
    private String k;

    /***
     * value: 存储值
     */
    private Object v;

    /**
     * 扩展值
     */
    private Object ext;

}
