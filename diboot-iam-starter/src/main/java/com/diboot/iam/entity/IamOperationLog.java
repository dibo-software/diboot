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
import com.baomidou.mybatisplus.annotation.TableName;
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
 *
 * @author mazc@dibo.ltd
 * @version v2.1.2
 * @date 2020/09/21
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("dbt_iam_operation_log")
public class IamOperationLog extends BaseEntity<String> {
    private static final long serialVersionUID = 8928160564300882271L;

    /**
     * 租户ID
     */
    @JsonIgnore
    @TableField
    private String tenantId;

    /**
     * 应用模块
     */
    @TableField
    private String appModule;

    /**
     * 业务对象
     */
    @Length(max = 100, message = "{validation.iamOperationLog.businessObj.Length.message}")
    @TableField()
    private String businessObj;

    /**
     * 操作
     */
    @Length(max = 100, message = "{validation.iamOperationLog.operation.Length.message}")
    @TableField()
    private String operation;

    /**
     * 用户类型
     */
    @Length(max = 100, message = "{validation.iamOperationLog.userType.Length.message}")
    @TableField()
    private String userType;

    /**
     * 用户ID
     */
    @TableField()
    private String userId;

    /**
     * 用户显示名
     */
    @Length(max = 100, message = "{validation.iamOperationLog.userRealname.Length.message}")
    @TableField()
    private String userRealname;

    /**
     * 请求uri
     */
    @Length(max = 500, message = "{validation.iamOperationLog.requestUri.Length.message}")
    @TableField()
    private String requestUri;

    /**
     * 请求method
     */
    @Length(max = 20, message = "{validation.iamOperationLog.requestMethod.Length.message}")
    @TableField()
    private String requestMethod;

    /**
     * 请求参数
     */
    @Length(max = 1000, message = "{validation.iamOperationLog.requestParams.Length.message}")
    @TableField()
    private String requestParams;

    /**
     * 请求IP
     */
    @Length(max = 50, message = "{validation.iamOperationLog.requestIp.Length.message}")
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
    @Length(max = 1000, message = "{validation.iamOperationLog.errorMsg.Length.message}")
    @TableField()
    private String errorMsg;

    public IamOperationLog setRequestParams(String requestParams) {
        if (V.notEmpty(requestParams) && requestParams.length() > 950) {
            requestParams = S.cut(requestParams, 950);
        }
        this.requestParams = requestParams;
        return this;
    }

}
