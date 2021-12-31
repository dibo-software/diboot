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
import com.diboot.mobile.vo.IamMemberVO;
import com.diboot.mobile.vo.WxMaSessionInfoVO;

/**
 * 微信扫码登录相关Service
 *
 * @author uu
 * @version : v2.3.1
 * @Copyright © diboot.com
 * @date 2021-08-31
 * Copyright © diboot.com
 */
public interface WxMaAuthService {

    /**
     * 获取登录后的session信息.
     *
     * @param jsCode – 登录时获取的 code
     * @return
     * @throws Exception
     */
    WxMaSessionInfoVO getSessionInfo(String jsCode) throws Exception;

    /**
     * 用户绑定小程序(先登陆，后绑定)
     *
     * 已经绑定提示已经绑定
     * 未绑定，IamMember自动创建，绑定IamUser
     *
     * @param wxInfoDTO
     * @return
     * @throws Exception
     */
    IamMember bindWxMa(WxMemberDTO wxInfoDTO) throws Exception;

    /**
     * 获取并保存用户
     *
     * 如果用户已经存在，那么直接返回
     * @param wxInfoDTO
     * @return
     * @throws Exception
     */
    IamMemberVO getAndSaveWxMember(WxMemberDTO wxInfoDTO) throws Exception;
}
