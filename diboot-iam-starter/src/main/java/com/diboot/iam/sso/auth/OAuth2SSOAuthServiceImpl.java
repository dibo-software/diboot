package com.diboot.iam.sso.auth;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.util.V;
import com.diboot.iam.auth.impl.BaseAuthServiceImpl;
import com.diboot.iam.config.Cons;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.shiro.IamAuthToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "diboot.iam.oauth2", name = {"client-id", "client-secret", "auth-center-url", "access-token-uri", "user-info-uri", "callback"})
public class OAuth2SSOAuthServiceImpl extends BaseAuthServiceImpl {

    public String getAuthType() {
        return Cons.DICTCODE_AUTH_TYPE.OAuth2.name();
    }

    @Override
    protected Wrapper buildQueryWrapper(IamAuthToken iamAuthToken) {
        // 查询最新的记录
        LambdaQueryWrapper<IamAccount> queryWrapper = Wrappers.<IamAccount>lambdaQuery()
                .select(IamAccount::getAuthAccount, IamAccount::getUserType, IamAccount::getUserId, IamAccount::getStatus)
                .eq(IamAccount::getUserType, iamAuthToken.getUserType())
                .eq(V.notEmpty(iamAuthToken.getTenantId()) ,IamAccount::getTenantId, iamAuthToken.getTenantId())
                .eq(IamAccount::getAuthAccount, iamAuthToken.getAuthAccount())
                .orderByDesc(IamAccount::getId);
        return queryWrapper;
    }
}
