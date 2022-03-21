/*
 * Copyright (c) 2015-2022, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.iam.vo;

import com.diboot.iam.entity.SystemConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * 系统配置VO定义
 *
 * @author wind
 * @version v2.5.0
 * @date 2022-01-13
 */
@Getter
@Setter
@Accessors(chain = true)
public class SystemConfigVO extends SystemConfig {
    private static final long serialVersionUID = -1032238711168691001L;

    /**
     * 属性标签
     */
    private String propLabel;

    /**
     * 默认值
     */
    private Object defaultValue;

    /**
     * 选项集
     */
    private Set<String> options;

    /**
     * 是否必填
     */
    private boolean required;

    /**
     * 序号
     */
    private int ordinal;

}
