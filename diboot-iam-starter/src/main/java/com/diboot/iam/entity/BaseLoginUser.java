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
package com.diboot.iam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.entity.BaseEntity;
import com.diboot.core.entity.BaseModel;
import com.diboot.core.util.S;
import com.diboot.core.vo.LabelValue;

import java.io.Serial;

/**
 * 可登录用户Base类定义
 * @author mazc@dibo.ltd
 * @version v2.1.0
 * @date 2020/06/28
 */
public abstract class BaseLoginUser extends BaseEntity<String> {
    private static final long serialVersionUID = 1689461104601728468L;

    /**
     * 获取显示名称
     * @return
     */
    public abstract String getDisplayName();

    /***
     * 获取当前用户类型
     * @return
     */
    public abstract String getUserType();

    /***
     * 获取当前用户租户id
     * @return
     */
    public abstract String getTenantId();

    /**
     * 附加对象，当前auth-token
     */
    @TableField(exist = false)
    private String authToken;

    /**
     * 附加对象，用于岗位等扩展
      */
    @TableField(exist = false)
    private LabelValue extensionObj;

    public LabelValue getExtensionObj(){
        return this.extensionObj;
    }
    public void setExtensionObj(LabelValue extensionObj){
        this.extensionObj = extensionObj;
    }

    public String getAuthToken(){
        return this.authToken;
    }
    public void setAuthToken(String authToken){
        this.authToken = authToken;
    }

    public String getUserTypeAndId(){
        return S.join(this.getUserType(), ":", this.getId());
    }

    @Override
    public String toString(){
        return this.authToken;
    }
}
