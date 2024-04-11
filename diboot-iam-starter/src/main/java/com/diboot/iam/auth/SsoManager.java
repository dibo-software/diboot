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
 */package com.diboot.iam.auth;

import com.diboot.iam.dto.SsoAuthorizeInfo;

import java.util.Map;

/**
 * SsoManager
 *
 * @author : fullstackyang
 * @version : v3.3.0
 * @Date 2023/03/11
 */
public interface SsoManager {

    /**
     * 获取认证类型
     * @return
     */
    String getAuthType();

    /**
     * 获取单点登录的登录地址
     * @param callback 前端回调地址
     * @return
     */
    SsoAuthorizeInfo getAuthorizeInfo(String callback);

    /**
     * 通过单点登录的凭证信息获取认证后的token
     * @param paramsMap 获取token的单点认证的凭证信息
     * @return
     */
    String getToken(Map<String, Object> paramsMap);
}
