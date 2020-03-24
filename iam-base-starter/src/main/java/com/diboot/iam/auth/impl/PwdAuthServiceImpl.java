package com.diboot.iam.auth.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.Encryptor;
import com.diboot.core.vo.Status;
import com.diboot.iam.auth.AuthService;
import com.diboot.iam.config.Cons;
import com.diboot.iam.dto.AuthCredential;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.entity.IamLoginTrace;
import com.diboot.iam.jwt.BaseJwtAuthToken;
import com.diboot.iam.service.IamAccountService;
import com.diboot.iam.service.IamLoginTraceService;
import com.diboot.iam.util.BeanUtils;
import com.diboot.iam.util.IamSecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户名密码认证的service实现
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/25
 */
@Service
@Slf4j
public class PwdAuthServiceImpl implements AuthService {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private IamAccountService accountService;
    @Autowired
    private IamLoginTraceService iamLoginTraceService;

    @Override
    public String getAuthType() {
        return Cons.DICTCODE_AUTH_TYPE.PWD.name();
    }

    @Override
    public IamAccount getAccount(BaseJwtAuthToken jwtToken) throws AuthenticationException {
        // 查询最新的记录
        LambdaQueryWrapper<IamAccount> queryWrapper = new LambdaQueryWrapper<IamAccount>()
                .select(IamAccount::getAuthAccount, IamAccount::getAuthSecret, IamAccount::getSecretSalt, IamAccount::getUserType, IamAccount::getUserId, IamAccount::getStatus)
                .eq(IamAccount::getUserType, jwtToken.getUserType())
                .eq(IamAccount::getAuthType, jwtToken.getAuthType())
                .eq(IamAccount::getAuthAccount, jwtToken.getAuthAccount())
                .orderByDesc(IamAccount::getId);
        IamAccount latestAccount = accountService.getSingleEntity(queryWrapper);
        if(latestAccount == null){
            return null;
        }
        if (Cons.DICTCODE_ACCOUNT_STATUS.I.name().equals(latestAccount.getStatus())) {
            throw new AuthenticationException("用户账号已禁用! account="+jwtToken.getAuthAccount());
        }
        if (Cons.DICTCODE_ACCOUNT_STATUS.L.name().equals(latestAccount.getStatus())) {
            throw new AuthenticationException("用户账号已锁定! account="+jwtToken.getAuthAccount());
        }
        if (isPasswordMatched(latestAccount, jwtToken) == false){
            throw new AuthenticationException("用户名或密码错误! account="+jwtToken.getAuthAccount());
        }
        return latestAccount;
    }

    @Override
    public String applyToken(AuthCredential credential) {
        BaseJwtAuthToken authToken = initBaseJwtAuthToken(credential);
        try {
            Subject subject = SecurityUtils.getSubject();
            subject.login(authToken);
            if (subject.isAuthenticated()) {
                log.debug("申请token成功！authtoken={}", authToken.getCredentials());
                saveLoginTrace(authToken, true);
                // 跳转到首页
                return (String) authToken.getCredentials();
            }
            else {
                log.error("认证失败");
                saveLoginTrace(authToken, false);
                throw new BusinessException(Status.FAIL_OPERATION, "认证失败");
            }
        } catch (Exception e) {
            log.error("登录异常", e);
            saveLoginTrace(authToken, false);
            throw new BusinessException(Status.FAIL_OPERATION, e.getMessage());
        }
    }

    /**
     * 用户名密码是否一致
     * @param account
     * @param jwtToken
     * @return
     */
    private static boolean isPasswordMatched(IamAccount account, BaseJwtAuthToken jwtToken){
        //加密后比较
        String encryptedStr = IamSecurityUtils.encryptPwd(jwtToken.getAuthSecret(), account.getSecretSalt());
        // 暂时兼容RC2版本，后期移除
        String oldEncryptedStr = Encryptor.encrypt(jwtToken.getAuthSecret(), account.getSecretSalt());
        return encryptedStr.equals(account.getAuthSecret()) || oldEncryptedStr.equals(account.getAuthSecret());
    }

    /**
     * 初始化JwtAuthToken实例
     * @param credential
     * @return
     */
    private BaseJwtAuthToken initBaseJwtAuthToken(AuthCredential credential){
        BaseJwtAuthToken token = new BaseJwtAuthToken(getAuthType(), credential.getUserTypeClass());
        // 设置账号密码
        token.setAuthAccount(credential.getAuthAccount()).setAuthSecret(credential.getAuthSecret());
        token.setRememberMe(credential.isRememberMe());
        // 生成token
        return token.generateAuthtoken(getExpiresInMinutes());
    }

    /**
     * 保存登录日志
     * @param authToken
     * @param isSuccess
     */
    protected void saveLoginTrace(BaseJwtAuthToken authToken, boolean isSuccess){
        IamLoginTrace loginTrace = new IamLoginTrace();
        loginTrace.setAuthType(getAuthType()).setAuthAccount(authToken.getAuthAccount()).setUserType(authToken.getUserType()).setSuccess(isSuccess);
        Object currentUser = IamSecurityUtils.getCurrentUser();
        if(currentUser != null){
            Long userId = (Long) BeanUtils.getProperty(currentUser, Cons.FieldName.id.name());
            loginTrace.setUserId(userId);
        }
        // 记录客户端信息
        String userAgent = request.getHeader("user-agent");
        String ipAddress = IamSecurityUtils.getRequestIp(request);
        loginTrace.setUserAgent(userAgent).setIpAddress(ipAddress);
        try{
            iamLoginTraceService.createEntity(loginTrace);
        }
        catch (Exception e){
            log.warn("保存登录日志异常", e);
        }
    }

}
