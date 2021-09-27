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
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.iam.util.JwtUtils;
import com.diboot.mobile.entity.IamMember;
import com.diboot.mobile.mapper.IamMemberMapper;
import com.diboot.mobile.service.IamMemberService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * 移动端用户service是实现类
 *
 * @author : uu
 * @version : v2.3.1
 * @Copyright © diboot.com
 * @Date 2021/8/31  14:08
 */
@Service
@Slf4j
public class IamMemberServiceImpl extends BaseServiceImpl<IamMemberMapper, IamMember> implements IamMemberService {

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
        return getSingleEntity(
                Wrappers.<IamMember>lambdaQuery().eq(IamMember::getOpenid, openid)
        );
    }
}
