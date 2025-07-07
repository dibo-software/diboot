/*
 * Copyright (c) 2015-2099, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.iam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.Encryptor;
import com.diboot.core.util.V;
import com.diboot.iam.entity.BaseLoginUser;
import com.diboot.iam.entity.IamLoginTrace;
import com.diboot.iam.mapper.IamLoginTraceMapper;
import com.diboot.iam.service.IamLoginTraceService;
import com.diboot.iam.shiro.IamAuthToken;
import com.diboot.iam.util.IamSecurityUtils;
import com.diboot.iam.util.TokenUtils;
import com.diboot.iam.vo.IamLoginTraceVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
* 登录记录相关Service实现
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-17
*/
@Service
@Slf4j
public class IamLoginTraceServiceImpl extends BaseServiceImpl<IamLoginTraceMapper, IamLoginTrace> implements IamLoginTraceService {

    @Override
    public boolean updateLogoutInfo(String token, String userType, String userId) {
        LambdaQueryWrapper<IamLoginTrace> queryWrapper = new QueryWrapper<IamLoginTrace>()
                .lambda()
                .select(IamLoginTrace::getId)
                .eq(IamLoginTrace::getUserType, userType)
                .eq(IamLoginTrace::getUserId, userId)
                .eq(IamLoginTrace::getIsSuccess, true)
                .orderByDesc(IamLoginTrace::getId);
        if (V.notEmpty(token)) {
            queryWrapper.eq(IamLoginTrace::getSignature, Encryptor.encrypt(token));
        }
        IamLoginTrace latestTrace = getSingleEntity(queryWrapper);
        if(latestTrace != null) {
            LambdaUpdateWrapper<IamLoginTrace> updateWrapper = new UpdateWrapper<IamLoginTrace>().lambda()
                    .set(IamLoginTrace::getLogoutTime, LocalDateTime.now())
                    .eq(IamLoginTrace::getId, latestTrace.getId());
            return super.update(updateWrapper);
        }
        return false;
    }

    @Override
    public void saveTokenRefreshTrace(String refreshToken, String oldToken) {
        String oldSignature = Encryptor.encrypt(oldToken);
        BaseLoginUser user = IamSecurityUtils.getCurrentUser();
        String newSignature = Encryptor.encrypt(refreshToken);
        LambdaUpdateWrapper<IamLoginTrace> updateWrapper = new LambdaUpdateWrapper<IamLoginTrace>()
                .set(IamLoginTrace::getSignature, newSignature)
                .set(IamLoginTrace::getSignType, IamLoginTrace.SIGN_TYPE.REFRESH_TOKEN.name())
                .eq(IamLoginTrace::getSignature, oldSignature);
        if(user != null){
            updateWrapper.eq(IamLoginTrace::getUserType, user.getUserType())
                    .eq(IamLoginTrace::getUserId, user.getId())
                    .eq(IamLoginTrace::getIsSuccess, true);
        }
        this.updateEntity(updateWrapper);
    }

    @Override
    public void appendLoginStatus(List<IamLoginTraceVO> voList) {
        if (V.isEmpty(voList)) {
            return;
        }
        for (IamLoginTraceVO vo : voList) {
            if (V.notEmpty(vo.getLogoutTime())) {
                vo.setOnlineStatus(IamLoginTraceVO.ONLINE_STATUS.LOGOUT.name());
                continue;
            }
            if (V.isEmpty(vo.getSignature())) {
                vo.setOnlineStatus(IamLoginTraceVO.ONLINE_STATUS.UNKNOWN.name());
                continue;
            }
            String token = Encryptor.decrypt(vo.getSignature());
            if (V.isEmpty(token)) {
                vo.setOnlineStatus(IamLoginTraceVO.ONLINE_STATUS.UNKNOWN.name());
                continue;
            }
            String cachedUserInfo = TokenUtils.getCachedUserInfoStr(token);
            if (V.isEmpty(cachedUserInfo)) {
                vo.setOnlineStatus(IamLoginTraceVO.ONLINE_STATUS.INVALID.name());
                continue;
            }
            IamAuthToken authToken = new IamAuthToken(cachedUserInfo);
            if (vo.isExpired(authToken.getExpiresInMinutes())) {
                vo.setOnlineStatus(IamLoginTraceVO.ONLINE_STATUS.INVALID.name());
                continue;
            }
            vo.setOnlineStatus(IamLoginTraceVO.ONLINE_STATUS.ONLINE.name());
        }
    }
}
