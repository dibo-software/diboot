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
package com.diboot.mobile.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.iam.auth.AuthServiceFactory;
import com.diboot.iam.config.Cons;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.service.IamAccountService;
import com.diboot.iam.util.IamSecurityUtils;
import com.diboot.mobile.dto.MobileCredential;
import com.diboot.mobile.entity.IamMember;
import com.diboot.mobile.service.IamMemberService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 微信公众号相关操作（登录用户为IamUser类型）
 *
 * @author : uu
 * @version : v2.4.0
 * @Copyright © diboot.com
 * @Date 2021/11/25  16:32
 */
@Slf4j
public class WxMpUserAuthServiceImpl extends WxMpMemberAuthServiceImpl {

    public WxMpUserAuthServiceImpl(WxMpService wxMpService, IamAccountService iamAccountService, IamMemberService iamMemberService, String STATE) {
        super(wxMpService, iamAccountService, iamMemberService, STATE);
    }

    @Override
    public IamMember bindWxMp(String code, String state) throws Exception {
        // 校验STATE
        if (V.notEmpty(STATE) && !STATE.equals(state)) {
            throw new BusinessException(Status.FAIL_INVALID_PARAM, "exception.business.wxMp.illegalState");
        }
        if (V.isEmpty(code)) {
            log.error("请求参数有误: code = null");
            throw new BusinessException(Status.FAIL_INVALID_PARAM, "exception.business.wxMp.nullcode");
        }
        IamUser iamUser = IamSecurityUtils.getCurrentUser();
        if (V.isEmpty(iamUser)) {
            throw new BusinessException(Status.FAIL_OPERATION, "exception.business.wx.bindAfterLogin");
        }
        WxOAuth2AccessToken accessToken = wxMpService.getOAuth2Service().getAccessToken(code);
        // 获取用户信息
        IamMember iamMember = iamMemberService.getSingleEntity(
                Wrappers.<IamMember>lambdaQuery()
                        .eq(IamMember::getUserType, IamUser.class.getSimpleName())
                        .eq(IamMember::getUserId, iamUser.getId())
        );
        if (V.notEmpty(iamMember)) {
            throw new BusinessException(Status.FAIL_OPERATION, "exception.business.wx.bindAgain");
        }
        // 创建绑定
        WxOAuth2UserInfo userInfo = wxMpService.getOAuth2Service().getUserInfo(accessToken, null);
        iamMember = mpInfo2IamMemberEntity(userInfo)
                .setUserId(iamUser.getId())
                .setOrgId(iamUser.getOrgId())
                .setUserType(IamUser.class.getSimpleName());
        iamMemberService.createEntity(iamMember);
        // 基于openId 创建iam_account账号
        IamAccount iamAccount = createIamAccountEntity(iamMember, iamMember.getUserId(), IamUser.class);
        iamAccountService.createEntity(iamAccount);
        return iamMember;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String applyToken(String code, String state) throws Exception {
        // 校验STATE
        if (V.notEmpty(STATE) && !STATE.equals(state)) {
            throw new BusinessException(Status.FAIL_INVALID_PARAM, "exception.business.wxMp.illegalState");
        }
        if (V.isEmpty(code)) {
            log.error("请求参数有误: code = null");
            throw new BusinessException(Status.FAIL_INVALID_PARAM, "exception.business.wxMp.nullcode");
        }
        WxOAuth2AccessToken accessToken = wxMpService.getOAuth2Service().getAccessToken(code);
        // 获取用户信息
        IamAccount account = iamAccountService.getSingleEntity(
                Wrappers.<IamAccount>lambdaQuery()
                        .eq(IamAccount::getUserType, IamUser.class.getSimpleName())
                        .eq(IamAccount::getAuthAccount, accessToken.getOpenId())
                        .eq(IamAccount::getAuthType, Cons.DICTCODE_AUTH_TYPE.WX_MP.name()));
        MobileCredential credential = new MobileCredential(accessToken.getOpenId());
        credential.setAuthType(Cons.DICTCODE_AUTH_TYPE.WX_MP.name());
        // 账户存在，直接登录
        if (V.isEmpty(account)) {
            throw new BusinessException(Status.FAIL_INVALID_PARAM, "exception.business.wx.shortcutLogin");
        }
        return AuthServiceFactory.getAuthService(Cons.DICTCODE_AUTH_TYPE.WX_MP.name()).applyToken(credential);
    }
}
