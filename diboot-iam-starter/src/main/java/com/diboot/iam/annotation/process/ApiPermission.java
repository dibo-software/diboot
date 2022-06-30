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
package com.diboot.iam.annotation.process;

import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.ApiUri;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
* 权限 Entity定义
 * @author JerryMa
 * @version v2.6.0
 * @date 2022/4/21
 * Copyright © diboot.com
*/
@Getter @Setter @Accessors(chain = true)
public class ApiPermission implements Serializable {
    private static final long serialVersionUID = -17224355139977969L;

    public ApiPermission(String code){
        this.code = code;
    }
    /**
     * 权限编码
     */
    private String code;
    /**
     * 接口地址定义
     */
    private List<ApiUri> apiUriList;

    public List<ApiUri> getApiUriList(){
        if(apiUriList == null){
            apiUriList = new ArrayList<>();
        }
        return apiUriList;
    }

    /**
     * 权限码的备注
     * @return
     */
    public String getLabel(){
        if(V.isEmpty(apiUriList)){
            return null;
        }
        List<String> labels = BeanUtils.collectToList(apiUriList, ApiUri::getLabel);
        return S.join(labels);
    }

}