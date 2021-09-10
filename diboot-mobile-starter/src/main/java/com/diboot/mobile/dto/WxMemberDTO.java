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
package com.diboot.mobile.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 小程序登陆或者获取手机号需要的DTO包装类
 *
 * @author : uu
 * @version : v2.3.1
 * @Copyright © diboot.com
 * @Date 2021/09/01  09:51
 */
@Getter
@Setter
public class WxMemberDTO implements Serializable {
    private static final long serialVersionUID = 6734098611185259658L;

    /**
     * 微信的用户唯一标示
     */
    private String openid;

    /**
     * 用户登陆后换取的sessionKey
     */
    private String sessionKey;

    /**
     * 使用 sha1( rawData + sessionkey ) 得到字符串，用于校验用户信息
     */
    private String signature;

    /**
     * 不包括敏感信息的原始数据字符串，用于计算签名
     */
    private String rawData;

    /**
     * 包括敏感数据在内的完整用户信息的加密数据
     */
    private String encryptedData;

    /**
     * 加密算法的初始向量
     */
    private String iv;

    private String nickName;

    private Integer gender;

    private String city;

    private String province;

    private String country;

    private String avatarUrl;

    private String unionId;
}
