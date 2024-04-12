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
package com.diboot.notification.entity;

import com.diboot.notification.annotation.BindVariable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 基础变量值
 *
 * @author : uu
 * @version : v1.0
 * @Date 2021/2/25  17:48
 * @Copyright © diboot.com
 */
@Getter
@Setter
@Accessors(chain = true)
public class BaseUserVariables implements Serializable {
    private static final long serialVersionUID = 6254327279941140819L;

    /**
     * 姓名
     */
    @BindVariable(name = "${用户姓名}")
    private String realName;

    /**
     * 标题
     */
    @BindVariable(name = "${标题}")
    private String title;

    /**
     * 手机号
     */
    @BindVariable(name = "${手机号}")
    private String phone;

    /**
     * 验证码
     */
    @BindVariable(name = "${验证码}")
    private String code;

}
