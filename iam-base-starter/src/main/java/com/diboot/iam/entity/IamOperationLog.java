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

import com.diboot.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

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

    //private String appModule;

    /**
     * 业务对象
     */
    private String businessObj;

    /**
     * 操作
     */
    private String operation;

    /**
     * 用户类型
     */
    private String userType;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户显示名
     */
    private String userRealname;

    /**
     * 请求uri
     */
    private String requestUri;

    /**
     * 请求method
     */
    private String requestMethod;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 请求IP
     */
    private String requestIp;

    /**
     * 状态码
     */
    private int statusCode;

    /**
     * 异常信息
     */
    private String errorMsg;

}
