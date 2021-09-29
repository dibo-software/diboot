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
package com.diboot.core.holder.api;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
* rest接口定义
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-03
*/
@Getter @Setter @Accessors(chain = true)
public class RestApi implements Serializable {
    private static final long serialVersionUID = -372368864183815006L;

    /**
     * 类别
     */
    private String category;

    /**
     * 接口名称
     */
    private String apiName;

    /**
     * 接口Method
     */
    private String apiMethod;

    /**
     * 接口URI
     */
    private String apiUri;

    /**
     * path路径参数
     */
    private List<Param> pathVariables;
    /**
     * url参数
     */
    private List<Param> urlParams;

}