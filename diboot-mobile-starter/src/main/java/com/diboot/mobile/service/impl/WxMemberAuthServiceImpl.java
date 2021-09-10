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
import com.diboot.core.binding.Binder;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.iam.auth.AuthServiceFactory;
import com.diboot.iam.config.Cons;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.service.IamAccountService;
import com.diboot.iam.util.JwtUtils;
import com.diboot.mobile.dto.MobileCredential;
import com.diboot.mobile.dto.WxMemberDTO;
import com.diboot.mobile.entity.IamMember;
import com.diboot.mobile.service.IamMemberService;
import com.diboot.mobile.service.WxMemberAuthService;
import com.diboot.mobile.vo.IamMemberVO;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

/**
 * 微信公众号相关操作
 *
 * @author : uu
 * @version : v2.3.1
 * @Copyright © diboot.com
 * @Date 2021/7/20  16:32
 */
@Slf4j
@Service
public class WxMemberAuthServiceImpl implements WxMemberAuthService {
    @Value("${wechat.state}")
    private String STATE;

    @Autowired(required = false)
    private WxMpService wxMpService;

    @Autowired
    private IamMemberService iamMemberService;

    @Autowired
    private IamAccountService iamAccountService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String applyLogin(String code, String state) throws Exception {
        // 校验STATE
        if (V.notEmpty(STATE) && !STATE.equals(state)) {
            throw new BusinessException(Status.FAIL_INVALID_PARAM, "非法来源");
        }
        if (V.isEmpty(code)) {
            log.error("请求参数有误: code = null");
            throw new BusinessException(Status.FAIL_INVALID_PARAM, "请求参数有误");
        }
        WxOAuth2AccessToken accessToken = wxMpService.getOAuth2Service().getAccessToken(code);
        // 获取用户信息
        IamAccount account = iamAccountService.getSingleEntity(
                Wrappers.<IamAccount>lambdaQuery()
                        .eq(IamAccount::getUserType, IamMember.class.getSimpleName())
                        .eq(IamAccount::getAuthAccount, accessToken.getOpenId())
                        .eq(IamAccount::getAuthType, Cons.DICTCODE_AUTH_TYPE.WX_MP.name()));
        // 账户存在，直接登陆
        if (V.notEmpty(account)) {
            MobileCredential credential = new MobileCredential(accessToken.getOpenId());
            return AuthServiceFactory.getAuthService(Cons.DICTCODE_AUTH_TYPE.WX_MP.name()).applyToken(credential);
        }
        // 账户不存在，表示首次进入，那么需要存储信息
        WxOAuth2UserInfo userInfo = wxMpService.getOAuth2Service().getUserInfo(accessToken, null);
        IamMember iamMember = mpInfo2IamMemberEntity(userInfo);
        iamMemberService.createEntity(iamMember);

        // 创建iam_account账号
        IamAccount iamAccount = createIamAccountEntity(iamMember);
        iamAccountService.createEntity(iamAccount);

        MobileCredential credential = new MobileCredential(accessToken.getOpenId());
        credential.setAuthType(Cons.DICTCODE_AUTH_TYPE.WX_MP.name());
        return AuthServiceFactory.getAuthService(Cons.DICTCODE_AUTH_TYPE.WX_MP.name()).applyToken(credential);
    }

    @Override
    public String getOpenId(HttpServletRequest request) throws Exception {
        Claims claimsFromRequest = JwtUtils.getClaimsFromRequest(request);
        String subject = claimsFromRequest.getSubject();
        String openId = subject.split(",")[0];
        if (V.isEmpty(openId)) {
            throw new BusinessException(Status.FAIL_INVALID_TOKEN, "获取用户信息失败");
        }
        return openId;
    }

    @Override
    public IamMember getIamMemberByOpenid(String openid) throws Exception {
        return iamMemberService.getSingleEntity(
                Wrappers.<IamMember>lambdaQuery().eq(IamMember::getOpenid, openid)
        );
    }

    @Override
    public boolean updateWxMemberPhone(WxMemberDTO wxMemberDTO) throws Exception {
        return false;
    }

    @Override
    public Object saveWxMember(WxMemberDTO wxInfoDTO) {
        // 校验用户是否存在，如果已经存在，那么直接返回数据
        IamMember iamMember = iamMemberService.getSingleEntity(
                Wrappers.<IamMember>lambdaQuery()
                        .eq(IamMember::getOpenid, wxInfoDTO.getOpenid())
                        .eq(IamMember::getStatus, IamMember.MEMBER_STATUS.NORMAL)
        );
        if (V.notEmpty(iamMember)) {
            return Binder.convertAndBindRelations(iamMember, IamMemberVO.class);
        }
        // 创建微信用户基本信息
        IamMember wxMember = maInfo2IamMemberEntity(wxInfoDTO);
        boolean success = iamMemberService.createEntity(wxMember);
        if (!success) {
            throw new BusinessException(Status.FAIL_OPERATION, "绑定用户信息失败！");
        }
        // 创建当前用户的账户
        IamAccount iamAccount = createIamAccountEntity(wxMember);
        success = iamAccountService.createEntity(iamAccount);
        if (!success) {
            throw new BusinessException(Status.FAIL_OPERATION, "绑定系统账户失败！");
        }
        return Binder.convertAndBindRelations(wxMember, IamMemberVO.class);
    }

    /**
     * 小程序用户信息构建IamMember
     *
     * @param userInfo
     * @return
     */
    private IamMember mpInfo2IamMemberEntity(WxOAuth2UserInfo userInfo) {
        return new IamMember()
                .setOpenid(userInfo.getOpenid())
                .setCountry(userInfo.getCountry())
                .setProvince(userInfo.getProvince())
                .setCity(userInfo.getCity())
                .setAvatarUrl(userInfo.getHeadImgUrl())
                .setGender(this.sex2gender(userInfo.getSex()))
                .setNickname(userInfo.getNickname())
                .setStatus(IamMember.MEMBER_STATUS.NORMAL.name());
    }

    /**
     * 小程序用户信息构建IamMember
     *
     * @param wxMemberDTO
     * @return
     */
    private IamMember maInfo2IamMemberEntity(WxMemberDTO wxMemberDTO) {
        return new IamMember().setOpenid(wxMemberDTO.getOpenid())
                .setNickname(wxMemberDTO.getNickName())
                .setGender(sex2gender(wxMemberDTO.getGender()))
                .setAvatarUrl(wxMemberDTO.getAvatarUrl())
                .setCountry(wxMemberDTO.getCountry())
                .setProvince(wxMemberDTO.getProvince())
                .setCity(wxMemberDTO.getCity())
                .setStatus(IamMember.MEMBER_STATUS.NORMAL.name());
    }

    /**
     * 根据用户基本信息创建账户信息
     *
     * @param iamMember
     * @return
     */
    private IamAccount createIamAccountEntity(IamMember iamMember) {
        IamAccount iamAccount = new IamAccount();
        iamAccount.setAuthAccount(iamMember.getOpenid())
                .setAuthType(Cons.DICTCODE_AUTH_TYPE.WX_MP.name())
                .setUserId(iamMember.getId())
                .setUserType(IamMember.class.getSimpleName())
                .setStatus(Cons.DICTCODE_ACCOUNT_STATUS.A.name());
        return iamAccount;
    }

    /***
     * 微信公众号信息中的性别编码转化为性别数据字典编码
     * @param sex
     * @return
     */
    private String sex2gender(Integer sex) {
        String gender = null;
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
