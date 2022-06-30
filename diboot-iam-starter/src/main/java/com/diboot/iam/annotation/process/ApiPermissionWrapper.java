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
package com.diboot.iam.annotation.process;

import com.diboot.core.util.V;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 接口权限wrapper
 * @author JerryMa
 * @version v2.6.0
 * @date 2022/4/21
 * Copyright © diboot.com
 */
@Getter @Setter
public class ApiPermissionWrapper implements Serializable {
    private static final long serialVersionUID = 7795636645748631729L;

    public ApiPermissionWrapper(){}

    public ApiPermissionWrapper(String name, String code){
        this.name = name;
        this.code = code;
    }

    /**
     * 类名
     */
    private String name;

    /**
     * 类别标题
      */
    private String code;

    /**
     * 子节点权限码集合
     */
    private List<ApiPermission> apiPermissionList;

    @JsonIgnore
    public boolean notEmpty(){
        return V.notEmpty(code) && V.notEmpty(apiPermissionList);
    }
}