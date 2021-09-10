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
package com.diboot.mobile.service;


import com.diboot.mobile.dto.WxMemberDTO;
import com.diboot.mobile.entity.IamMember;

import javax.servlet.http.HttpServletRequest;

/**
 * 微信扫码登录相关Service
 *
 * @author uu
 * @version : v2.3.1
 * @Copyright © diboot.com
 * @date 2021-08-31
 * Copyright © diboot.com
 */
public interface WxMemberAuthService {

    /**
     * 申请登陆
     *
     * @param code
     * @param state
     * @return
     * @throws Exception
     */
    String applyLogin(String code, String state) throws Exception;

    /**
     * 公众号使用：根据token获取openId
     *
     * @param request
     * @return
     * @throws Exception
     */
    String getOpenId(HttpServletRequest request) throws Exception;

    /**
     * 公众号使用：根据token获取IamMember
     *
     * @param openid
     * @return
     * @throws Exception
     */
    IamMember getIamMemberByOpenid(String openid)  throws Exception;

    /**
     * 更新用户手机号
     *
     * @param wxMemberDTO
     * @return
     * @throws Exception
     */
    boolean updateWxMemberPhone(WxMemberDTO wxMemberDTO) throws Exception;

    Object saveWxMember(WxMemberDTO wxInfoDTO);
}
