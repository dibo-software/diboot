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
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

/**
 * 操作日志
 * @author mazc@dibo.ltd
 * @version v2.1.2
 * @date 2020/09/21
 */
@Getter
@Setter
@Accessors(chain = true)
public class IamOperationLog extends BaseEntity {
    private static final long serialVersionUID = 8928160564300882271L;

    /**
     * 租户ID
     */
    @JsonIgnore
    @TableField
    private Long tenantId;

    /**
     * 应用模块
      */
    @TableField
    private String appModule;

    /**
     * 业务对象
     */
    @Length(max = 100, message = "业务对象长度应小于100")
    @TableField()
    private String businessObj;

    /**
     * 操作
     */
    @Length(max = 100, message = "操作长度应小于100")
    @TableField()
    private String operation;

    /**
     * 用户类型
     */
    @Length(max = 100, message = "用户类型长度应小于100")
    @TableField()
    private String userType;

    /**
     * 用户ID
     */
    @TableField()
    private Long userId;

    /**
     * 用户显示名
     */
    @Length(max = 100, message = "用户类型长度应小于100")
    @TableField()
    private String userRealname;

    /**
     * 请求uri
     */
    @Length(max = 500, message = "用户类型长度应小于500")
    @TableField()
    private String requestUri;

    /**
     * 请求method
     */
    @Length(max = 20, message = "用户类型长度应小于20")
    @TableField()
    private String requestMethod;

    /**
     * 请求参数
     */
    @Length(max = 1000, message = "用户类型长度应小于1000")
    @TableField()
    private String requestParams;

    /**
     * 请求IP
     */
    @Length(max = 50, message = "用户类型长度应小于50")
    @TableField()
    private String requestIp;

    /**
     * 状态码
     */
    @TableField()
    private int statusCode;

    /**
     * 异常信息
     */
    @Length(max = 1000, message = "用户类型长度应小于1000")
    @TableField()
    private String errorMsg;

    public IamOperationLog setRequestParams(String requestParams){
        if(V.notEmpty(requestParams) && requestParams.length() > 980){
            requestParams = S.cut(requestParams, 980);
        }
        this.requestParams = requestParams;
        return this;
    }

}
