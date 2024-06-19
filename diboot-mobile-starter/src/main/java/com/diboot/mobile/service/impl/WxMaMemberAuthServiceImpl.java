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

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.binding.Binder;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.iam.config.Cons;
import com.diboot.iam.entity.BaseLoginUser;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.service.IamAccountService;
import com.diboot.mobile.dto.WxMemberDTO;
import com.diboot.mobile.entity.IamMember;
import com.diboot.mobile.service.IamMemberService;
import com.diboot.mobile.service.WxMaAuthService;
import com.diboot.mobile.vo.IamMemberVO;
import com.diboot.mobile.vo.WxMaSessionInfoVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;

/**
 * 微信公众号相关操作（登录用户为IamMember类型）
 *
 * @author : uu
 * @version : v2.3.1
 * @Copyright © diboot.com
 * @Date 2021/7/20  16:32
 */
@Slf4j
@AllArgsConstructor
public class WxMaMemberAuthServiceImpl implements WxMaAuthService {

    protected final WxMaService wxMaService;

    protected final IamMemberService iamMemberService;

    protected final IamAccountService iamAccountService;

    @Override
    public WxMaSessionInfoVO getSessionInfo(String jsCode) throws WxErrorException {
        WxMaJscode2SessionResult sessionInfo = wxMaService.getUserService().getSessionInfo(jsCode);
        return new WxMaSessionInfoVO()
                .setSessionKey(sessionInfo.getSessionKey())
                .setOpenid(sessionInfo.getOpenid())
                .setUnionid(sessionInfo.getUnionid());
    }

    @Override
    public IamMember bindWxMa(WxMemberDTO wxInfoDTO) throws Exception {
        throw new BusinessException(Status.FAIL_OPERATION, "exception.business.wx.notSupportBind");
    }

    @Override
    public IamMemberVO getAndSaveWxMember(WxMemberDTO wxInfoDTO) {
        // 校验用户是否存在，如果已经存在，那么直接返回数据
        IamMember iamMember = iamMemberService.getSingleEntity(
                Wrappers.<IamMember>lambdaQuery()
                        .eq(IamMember::getOpenid, wxInfoDTO.getOpenid())
                        .eq(IamMember::getStatus, Cons.DICTCODE_ACCOUNT_STATUS.A.name())
        );
        if (V.notEmpty(iamMember)) {
            return Binder.convertAndBindRelations(iamMember, IamMemberVO.class);
        }
        // 创建微信用户基本信息
        IamMember wxMember = maInfo2IamMemberEntity(wxInfoDTO)
                .setUserId(Cons.ID_PREVENT_NULL).setOrgId(Cons.ID_PREVENT_NULL)
                .setUserType(IamMember.class.getSimpleName());
        boolean success = iamMemberService.createEntity(wxMember);
        if (!success) {
            throw new BusinessException(Status.FAIL_OPERATION, "exception.business.wx.bindMemberFailed");
        }
        // 创建当前用户的账户
        IamAccount iamAccount = createIamAccountEntity(wxMember, wxMember.getId(), IamMember.class);
        success = iamAccountService.createEntity(iamAccount);
        if (!success) {
            throw new BusinessException(Status.FAIL_OPERATION, "exception.business.wx.createAccountFailed");
        }
        return Binder.convertAndBindRelations(wxMember, IamMemberVO.class);
    }

    /**
     * 小程序用户信息构建IamMember
     *
     * @param wxMemberDTO
     * @return
     */
    protected IamMember maInfo2IamMemberEntity(WxMemberDTO wxMemberDTO) {
        return new IamMember().setOpenid(wxMemberDTO.getOpenid())
                .setNickname(wxMemberDTO.getNickName())
                .setGender(sex2gender(wxMemberDTO.getGender()))
                .setAvatarUrl(wxMemberDTO.getAvatarUrl())
                .setCountry(wxMemberDTO.getCountry())
                .setProvince(wxMemberDTO.getProvince())
                .setCity(wxMemberDTO.getCity())
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
                .setUserId(userId).setUserType(userCls.getSimpleName())
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
