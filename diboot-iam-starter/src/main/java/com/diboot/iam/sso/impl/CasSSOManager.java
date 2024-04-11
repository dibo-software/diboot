package com.diboot.iam.sso.impl;

import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.V;
import com.diboot.iam.auth.AuthServiceFactory;
import com.diboot.iam.sso.SSOManager;
import com.diboot.iam.config.Cons;
import com.diboot.iam.dto.SsoAuthorizeInfo;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.sso.credential.CasCredential;
import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.TicketValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.Map;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "diboot.iam.cas-server", name = {"auth-center-url", "login-url", "callback"})
public class CasSSOManager implements SSOManager {

    private static final String TICKET_KEY = "ticket";


    @Value("${diboot.iam.cas-server.auth-center-url}")
    private String authCenterUrl;
    @Value("${diboot.iam.cas-server.login-url}")
    private String loginUrl;
    @Value("${diboot.iam.cas-server.logout-url}")
    private String logoutUrl;
    @Value("${diboot.iam.cas-server.callback}")
    private String callback;

    @Override
    public String getAuthType() {
        return Cons.DICTCODE_AUTH_TYPE.CAS_SERVER.name();
    }

    @Override
    public SsoAuthorizeInfo getAuthorizeInfo(String callback) {
        if (V.isEmpty(callback)) {
            callback = this.callback;
        }
        if (V.isEmpty(callback)) {
            throw new BusinessException("回调地址为空");
        }
        try {
            callback = URLEncoder.encode(callback, "UTF-8");
        } catch(Exception e) {
            log.error("URL编码出错", e);
        }
        String url = loginUrl + "?service=" + callback;
        SsoAuthorizeInfo authorizeInfo = new SsoAuthorizeInfo(url);
        return authorizeInfo;
    }

    @Override
    public String getToken(Map<String, Object> paramsMap) {
        if (V.isEmpty(paramsMap) || paramsMap.get(TICKET_KEY) == null) {
            return null;
        }
        String ticket = String.valueOf(paramsMap.get(TICKET_KEY));
        Cas20ServiceTicketValidator ticketValidator = new Cas20ServiceTicketValidator(this.authCenterUrl);
        try {
            Assertion assertion = ticketValidator.validate(ticket, this.callback);
            String username = assertion.getPrincipal().getName();
            // 登录认证后获取token
            System.out.println("User logged in: " + username);
            // 在这里可以完成用户登录逻辑，例如设置用户会话等
            CasCredential credential = new CasCredential();
            credential.setAuthAccount(username).setUserType(IamUser.class.getSimpleName()).setAuthType(Cons.DICTCODE_AUTH_TYPE.CAS_SERVER.name());
            return AuthServiceFactory.getAuthService(Cons.DICTCODE_AUTH_TYPE.CAS_SERVER.name()).applyToken(credential);
        } catch (TicketValidationException e) {
            log.error("CAS Ticket 验证失败", e);
            throw new BusinessException("Ticket验证失败");
        }
    }
}
