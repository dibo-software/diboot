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
import com.diboot.iam.entity.BaseLoginUser;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.service.IamAccountService;
import com.diboot.mobile.dto.MobileCredential;
import com.diboot.mobile.entity.IamMember;
import com.diboot.mobile.service.IamMemberService;
import com.diboot.mobile.service.WxMpAuthService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 微信公众号相关操作（登录用户为IamMember类型）
 *
 * @author : uu
 * @version : v2.4.0
 * @Copyright © diboot.com
 * @Date 2021/7/20  16:32
 */
@Slf4j
public class WxMpMemberAuthServiceImpl implements WxMpAuthService {

    protected String STATE;

    protected WxMpService wxMpService;

    protected IamMemberService iamMemberService;

    protected IamAccountService iamAccountService;

    public WxMpMemberAuthServiceImpl(WxMpService wxMpService, IamAccountService iamAccountService, IamMemberService iamMemberService, String STATE) {
        this.wxMpService = wxMpService;
        this.iamMemberService = iamMemberService;
        this.iamAccountService = iamAccountService;
        this.STATE = STATE;
    }

    @Override
    public String buildOAuthUrl4mp(String url) throws Exception {
        return wxMpService.getOAuth2Service()
                .buildAuthorizationUrl(url, WxConsts.OAuth2Scope.SNSAPI_USERINFO, STATE);
    }

    @Override
    public IamMember bindWxMp(String code, String state) throws Exception {
        throw new BusinessException(Status.FAIL_OPERATION, "exception.business.wx.notSupportBind");
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
                        .eq(IamAccount::getUserType, IamMember.class.getSimpleName())
                        .eq(IamAccount::getAuthAccount, accessToken.getOpenId())
                        .eq(IamAccount::getAuthType, Cons.DICTCODE_AUTH_TYPE.WX_MP.name()));
        MobileCredential credential = new MobileCredential(accessToken.getOpenId());
        credential.setAuthType(Cons.DICTCODE_AUTH_TYPE.WX_MP.name());
        credential.setUserTypeClass(IamMember.class);
        // 账户存在，直接登录
        if (V.notEmpty(account)) {
            return AuthServiceFactory.getAuthService(Cons.DICTCODE_AUTH_TYPE.WX_MP.name()).applyToken(credential);
        }
        // 账户不存在，表示首次进入，那么需要存储信息
        WxOAuth2UserInfo userInfo = wxMpService.getOAuth2Service().getUserInfo(accessToken, null);
        IamMember iamMember = mpInfo2IamMemberEntity(userInfo)
                .setUserId(Cons.ID_PREVENT_NULL).setOrgId(Cons.ID_PREVENT_NULL)
                .setUserType(IamMember.class.getSimpleName());
        iamMemberService.createEntity(iamMember);
        // 创建iam_account账号
        IamAccount iamAccount = createIamAccountEntity(iamMember, iamMember.getId(), IamMember.class);
        iamAccountService.createEntity(iamAccount);
        return AuthServiceFactory.getAuthService(Cons.DICTCODE_AUTH_TYPE.WX_MP.name()).applyToken(credential);
    }

    /**
     * 公众号用户信息构建IamMember
     *
     * @param userInfo
     * @return
     */
    protected IamMember mpInfo2IamMemberEntity(WxOAuth2UserInfo userInfo) {
        return new IamMember()
                .setOpenid(userInfo.getOpenid())
                .setAvatarUrl(userInfo.getHeadImgUrl())
                .setNickname(userInfo.getNickname())
                .setStatus(Cons.DICTCODE_ACCOUNT_STATUS.A.name());
    }

    /**
     * 根据用户基本信息创建账户信息
     *
     * @param iamMember
     * @return
     */
    protected IamAccount createIamAccountEntity(IamMember iamMember, String userId, Class<? extends BaseLoginUser> userCls) {
        return new IamAccount().setAuthAccount(iamMember.getOpenid())
                .setAuthType(Cons.DICTCODE_AUTH_TYPE.WX_MP.name())
                .setUserId(userId)
                .setUserType(userCls.getSimpleName())
                .setStatus(Cons.DICTCODE_ACCOUNT_STATUS.A.name());
    }

    /***
     * 微信公众号信息中的性别编码转化为性别数据字典编码
     * @param sex
     * @return
     */
    protected String sex2gender(Integer sex) {
        String gender;
        if (V.equals(sex, 1)) {
            gender = "M";
        } else if (V.equals(sex, 2)) {
            gender = "F";
        } else {
            gender = "UK";
        }
        return gender;
    }
}
