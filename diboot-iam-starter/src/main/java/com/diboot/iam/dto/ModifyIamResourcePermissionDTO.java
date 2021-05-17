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
package com.diboot.iam.dto;

import com.diboot.core.util.S;
import com.diboot.core.util.V;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 修改权限DTO
 *
 * @author : uu
 * @version : v1.0
 * @Date 2021/5/8  12:49
 * @copyright www.diboot.com
 */
@Getter
@Setter
@Accessors(chain = true)
public class ModifyIamResourcePermissionDTO implements Serializable {
    private static final long serialVersionUID = 211276433727981441L;

    private Long id;

    private String apiSet;

    private List<String> apiSetList;

    /***
     * 设置接口列表
     * @param apiSetList
     */
    public void setApiSetList(List<String> apiSetList) {
        if (V.isEmpty(apiSetList)){
            this.setApiSet(null);
        }
        this.setApiSet(S.join(apiSetList, ","));
    }
}
