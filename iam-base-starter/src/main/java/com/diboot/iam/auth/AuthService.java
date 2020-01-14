package com.diboot.iam.auth;

import com.diboot.iam.dto.AuthCredential;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.jwt.BaseJwtAuthToken;
import com.diboot.iam.util.JwtUtils;
import org.apache.shiro.authc.AuthenticationException;

/**
 * 账号认证的service
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/25
 */
public interface AuthService {

    /**
     * 获取认证类型
     * @return
     */
    String getAuthType();

    /**
     * 过期分钟数
     * @return
     */
    default int getExpiresInMinutes(){
        return JwtUtils.EXPIRES_IN_MINUTES;
    }

    /**
     * 获取用户
     * @param jwtToken
     * @return
     * @throws AuthenticationException
     */
    IamAccount getAccount(BaseJwtAuthToken jwtToken) throws AuthenticationException;

    /**
     * 申请Token
     * @param credential 登录凭证
     * @return token JWT Token
     */
    String applyToken(AuthCredential credential);

}
