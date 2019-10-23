package com.diboot.shiro.jwt;

import com.diboot.core.util.JSON;
import com.diboot.core.util.V;
import com.diboot.shiro.config.AuthType;
import com.diboot.shiro.entity.TokenAccountInfo;
import com.diboot.shiro.enums.IUserType;
import com.diboot.shiro.service.AuthWayService;
import com.diboot.shiro.util.JwtHelper;
import org.apache.shiro.authc.AuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
@Component
public class BaseJwtAuthenticationToken implements AuthenticationToken {

    private static final Logger logger = LoggerFactory.getLogger(BaseJwtAuthenticationToken.class);

    /**登录用的账号（此处的这个账号是一种抽象的概念*/
    private String account;

    /**登录用的密码 （此处的这个密码也是一种抽象的概念*/
    private String password;

    /**登录使用方式*/
    private AuthType authType;

    /**
     * 用户类型枚举
     */
    private IUserType iUserType;

    /**
     * 可登陆状态
     */
    private List<String> statusList;

    /**authz token*/
    private String authtoken;

    /**申请token的密码*/
    private String applyTokenSecret;

    /**签名key (默认SIGN_KEY，配置signKey, 或微信state, 密码等)*/
    private String signKey = JwtHelper.SIGN_KEY;

    /**过期时间*/
    private long expiresInMinutes = JwtHelper.EXPIRES_IN_MINUTES;

    private Map<String, AuthWayService> authWayServiceMap;

    /**默认构造函数*/
    public BaseJwtAuthenticationToken(){

    }

    public BaseJwtAuthenticationToken(Map<String, AuthWayService> authWayServiceMap){
        this.authWayServiceMap = authWayServiceMap;
    }

    /***
     * 用户名码形式的授权
     * @param authWayServiceMap  //所有认证业务service
     * @param account
     * @param password
     * @param iUserType     //用户类型
     */
    public BaseJwtAuthenticationToken(Map<String, AuthWayService> authWayServiceMap,
                                      String account, String password, IUserType iUserType,
                                      List<String> statusList){
        this.authWayServiceMap = authWayServiceMap;
        this.account = account;
        this.password = password;
        // 设置为默认登录方式
        this.authType = AuthType.USERNAME_PASSWORD;
        this.iUserType = iUserType;
        this.statusList = statusList;

        this.initJwtAuthenticationToken(account, signKey, false);
    }

    /***
     * 以用户名密码这类形式的其他类型授权
     * @param authWayServiceMap //所有认证业务service map
     * @param account
     * @param password
     * @param authType          //具体认证业务类型
     * @param iUserType
     */
    public BaseJwtAuthenticationToken(Map<String, AuthWayService> authWayServiceMap,
                                      String account, String password, AuthType authType,
                                      IUserType iUserType, List<String> statusList){
        this.authWayServiceMap = authWayServiceMap;
        this.account = account;
        this.password = password;
        this.authType = authType;
        this.iUserType = iUserType;
        this.statusList = statusList;

        this.initJwtAuthenticationToken(account, signKey, getAuthWayService().isPreliminaryVerified());
    }

    /***
     * 其他授权种类的适配构造函数
     * @param authWayServiceMap
     * @param account
     * @param authType
     */
    public BaseJwtAuthenticationToken(Map<String, AuthWayService> authWayServiceMap, String account, AuthType authType){
        this.authWayServiceMap = authWayServiceMap;
        this.account = account;
        this.authType = authType;
        this.initJwtAuthenticationToken(account, signKey, getAuthWayService().isPreliminaryVerified());
    }

    public AuthWayService getAuthWayService(){

        if (V.notEmpty(authWayServiceMap)){

            for (AuthWayService authWayService : authWayServiceMap.values()){
                if (authWayService.authType() == authType){
                    authWayService.initByToken(this);
                    return authWayService;
                }
            }
        }
        return null;
    }

    /***
     * 初始化认证token
     * @param account
     * @param password
     * @param preliminaryVerified
     */
    private void initJwtAuthenticationToken(String account, String password, boolean preliminaryVerified){
        if(this.account != null){
            Long expiresInMinutes = this.getAuthWayService().getExpiresInMinutes();
            this.expiresInMinutes = V.notEmpty(expiresInMinutes) ? expiresInMinutes : this.expiresInMinutes;
            String user = JSON.stringify(new TokenAccountInfo(this.account, this.iUserType.getType()));
            this.authtoken = JwtHelper.generateToken(user, this.signKey, this.expiresInMinutes);
        }
    }

    @Override
    public Object getPrincipal() {
        return account;
    }

    @Override
    public Object getCredentials() {
        return authtoken;
    }

    public String getApplyTokenSecret() {
        return applyTokenSecret;
    }

    /***
     * 验证失败的时候清空token
     */
    public void clearAuthtoken(){
        this.authtoken = null;
    }

    public void setApplyTokenSecret(String applyTokenSecret) {
        this.applyTokenSecret = applyTokenSecret;
    }

    public String getSignKey() {
        return signKey;
    }

    public void setSignKey(String signKey) {
        this.signKey = signKey;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AuthType getAuthType() {
        return authType;
    }

    public void setAuthType(AuthType authType) {
        this.authType = authType;
    }

    public String getAuthtoken() {
        return authtoken;
    }

    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }

    public long getExpiresInMinutes() {
        return expiresInMinutes;
    }

    public void setExpiresInMinutes(long expiresInMinutes) {
        this.expiresInMinutes = expiresInMinutes;
    }

    public Map<String, AuthWayService> getAuthWayServiceMap() {
        return authWayServiceMap;
    }

    public void setAuthWayServiceMap(Map<String, AuthWayService> authWayServiceMap) {
        this.authWayServiceMap = authWayServiceMap;
    }

    public IUserType getIUserType() {
        return iUserType;
    }

    public void setIUserType(IUserType iUserType) {
        this.iUserType = iUserType;
    }

    public List<String> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<String> statusList) {
        this.statusList = statusList;
    }
}
