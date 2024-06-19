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
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.binding.Binder;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.iam.config.Cons;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.service.IamAccountService;
import com.diboot.iam.util.IamSecurityUtils;
import com.diboot.mobile.dto.WxMemberDTO;
import com.diboot.mobile.entity.IamMember;
import com.diboot.mobile.service.IamMemberService;
import com.diboot.mobile.vo.IamMemberUserVO;
import com.diboot.mobile.vo.IamMemberVO;
import lombok.extern.slf4j.Slf4j;

/**
 * 微信小程序相关操作（登录用户为IamUser类型）
 *
 * @author : uu
 * @version : v2.4.0
 * @Copyright © diboot.com
 * @Date 2021/11/25  16:32
 */
@Slf4j
public class WxMaUserAuthServiceImpl extends WxMaMemberAuthServiceImpl {

    public WxMaUserAuthServiceImpl(WxMaService wxMaService, IamMemberService iamMemberService, IamAccountService iamAccountService) {
        super(wxMaService, iamMemberService, iamAccountService);
    }

    @Override
    public IamMember bindWxMa(WxMemberDTO wxInfoDTO) throws Exception {
        IamUser iamUser = IamSecurityUtils.getCurrentUser();
        if (V.isEmpty(iamUser)) {
            throw new BusinessException(Status.FAIL_OPERATION, "exception.business.wx.bindAfterLogin");
        }
        // 获取用户信息
        IamMember iamMember = iamMemberService.getSingleEntity(
                Wrappers.<IamMember>lambdaQuery()
                        .eq(IamMember::getUserType, IamUser.class.getSimpleName())
                        .eq(IamMember::getUserId, iamUser.getId())
        );
        if (V.notEmpty(iamMember)) {
            throw new BusinessException(Status.FAIL_OPERATION, "exception.business.wx.bindAgain");
        }
        iamMember = maInfo2IamMemberEntity(wxInfoDTO)
                .setUserId(iamUser.getId())
                .setOrgId(iamUser.getOrgId())
                .setUserType(IamUser.class.getSimpleName());
        boolean success = iamMemberService.createEntity(iamMember);
        if (!success) {
            throw new BusinessException(Status.FAIL_OPERATION, "exception.business.wx.bindMemberFailed");
        }
        // 创建当前用户的账户
        IamAccount iamAccount = createIamAccountEntity(iamMember, iamMember.getUserId(), IamUser.class);
        success = iamAccountService.createEntity(iamAccount);
        if (!success) {
            throw new BusinessException(Status.FAIL_OPERATION, "exception.business.wx.createAccountFailed");
        }
        return iamMember;
    }

    @Override
    public IamMemberVO getAndSaveWxMember(WxMemberDTO wxInfoDTO) {
        // 校验用户是否存在，如果已经存在，那么直接返回数据
        IamMember iamMember = iamMemberService.getSingleEntity(
                Wrappers.<IamMember>lambdaQuery()
                        .eq(IamMember::getOpenid, wxInfoDTO.getOpenid())
                        .eq(IamMember::getStatus, Cons.DICTCODE_ACCOUNT_STATUS.A.name())
        );
        // 账户存在，直接登录
        if (V.isEmpty(iamMember)) {
            throw new BusinessException(Status.FAIL_INVALID_PARAM, "exception.business.wx.shortcutLogin");
        }
        return Binder.convertAndBindRelations(iamMember, IamMemberUserVO.class);
    }
}
