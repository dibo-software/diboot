package com.diboot.example.controller;

import com.diboot.core.config.BaseConfig;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Status;
import com.diboot.shiro.config.AuthType;
import com.diboot.shiro.entity.SysUser;
import com.diboot.shiro.jwt.BaseJwtAuthenticationToken;
import com.diboot.shiro.service.AuthWayService;
import com.diboot.shiro.service.SysUserService;
import com.diboot.shiro.util.JwtHelper;
import com.diboot.shiro.wx.mp.service.WxMpServiceExt;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthTokenController {

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenController.class);

    private static final String STATE = BaseConfig.getProperty("wechat.state");

    @Autowired
    private Map<String, AuthWayService> authWayServiceMap;

    @Autowired
    private WxMpServiceExt wxMpService;

    @Autowired
    private SysUserService sysUserService;


    /**
     * 注册用户： 注册成功之后会自动登陆
     * @param sysUser
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @PostMapping("/register")
    public JsonResult register(@RequestBody SysUser sysUser, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String password = sysUser.getPassword();
        boolean register = sysUserService.register(sysUser);
        if (register) {
            //注册成功后自动登陆:注册后密码被加密，重新设置为不加密的密码然后进行登陆
            sysUser.setPassword(password);
            return login(sysUser, request, response);
        }
        return new JsonResult(Status.FAIL_OPERATION);
    }
    /***
     * 用户名密码登录接口
     * @param sysUser
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @PostMapping("/login")
    public JsonResult login(@RequestBody SysUser sysUser, HttpServletRequest request, HttpServletResponse response) throws Exception{
        String errorMsg = "登录失败";
        try{
            BaseJwtAuthenticationToken authToken = new BaseJwtAuthenticationToken(authWayServiceMap, sysUser.getUsername(), sysUser.getPassword(), AuthType.USERNAME_PASSWORD);
            Subject subject = SecurityUtils.getSubject();
            subject.login(authToken);

            if (subject.isAuthenticated()){
                logger.debug("申请token成功！authtoken="+authToken.getCredentials());
                String token = (String)authToken.getCredentials();
                // 跳转到首页
                return new JsonResult(token, "Token申请成功");
            }
        }
        catch (Exception e) {
            logger.error("登录失败", e);
        }


        return new JsonResult(Status.FAIL_INVALID_TOKEN, errorMsg);
    }


    /**
     * 创建OAuth认证链接(为了获取授权所需code)
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping("/buildOAuthUrl")
    public JsonResult buildOAuthUrl4mp(HttpServletRequest request) throws Exception{
        String url = request.getParameter("url");
        if (V.isEmpty(url)){
            return new JsonResult(Status.FAIL_OPERATION, new String[]{"url为空，获取OAuth链接失败"});
        }

        String oauthUrl = wxMpService.oauth2buildAuthorizationUrl(url, WxConsts.OAuth2Scope.SNSAPI_USERINFO, STATE);
        return new JsonResult(Status.OK, oauthUrl, new String[]{"获取OAuth链接成功"});
    }

    /**
     * 微信公众号的回调授权登录认证
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/apply")
    public JsonResult applyTokenByOAuth2mp(@RequestParam("code") String code,
                                           @RequestParam("state") String state,
                                           HttpServletRequest request) throws Exception{
        String openid = "";
        if (JwtHelper.isRequestTokenEffective(request)){
            String account = JwtHelper.getAccountFromToken(JwtHelper.getRequestToken(request));
            if (account == null){
                // 如果有code并且token已过期，则使用code获取openid
                if (V.isEmpty(code)){
                    return new JsonResult(Status.FAIL_INVALID_TOKEN, new String[]{"token已过期"});
                }
            } else {
                openid = account;
            }
        }

        // 如果openid没有通过token获取到，则通过code获取
        if (V.isEmpty(openid)){
            // 校验STATE
            if (V.notEmpty(STATE) && !STATE.equals(state)){
                return new JsonResult(Status.FAIL_OPERATION, new String[]{"非法来源"});
            }
            // 获取wxMpService
            WxMpOAuth2AccessToken wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
            if (!wxMpService.oauth2validateAccessToken(wxMpOAuth2AccessToken)){
                wxMpOAuth2AccessToken = wxMpService.oauth2refreshAccessToken(wxMpOAuth2AccessToken.getRefreshToken());
            }
            WxMpUser wxMpUser = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);

            openid = wxMpUser.getOpenId();
        }

        // 如果没有获取到wechat，则返回提示信息
        if (V.isEmpty(openid)){
            return new JsonResult(Status.FAIL_INVALID_TOKEN, new String[]{"获取信息失败"});
        }

        // 设置token
        BaseJwtAuthenticationToken authToken = new BaseJwtAuthenticationToken(authWayServiceMap, openid, AuthType.WX_MP);
        // 获取当前的Subject
        Subject subject = SecurityUtils.getSubject();
        String token = null;
        String errorMsg = null;

        try {
            subject.login(authToken);
            //验证是否登录成功
            if(subject.isAuthenticated()){
                token = (String)authToken.getCredentials();
                logger.debug("openid[{}]申请token成功！authtoken={}", openid, token);
            }
        }
        catch(Exception e){
            logger.error("登录失败", e);
        }

        if (V.isEmpty(token)){
            String msg = V.notEmpty(errorMsg) ? errorMsg : "申请token失败";
            return new JsonResult(Status.FAIL_INVALID_TOKEN, new String[]{msg});
        }

        return new JsonResult(Status.OK, token, new String[]{"申请token成功"});
    }

    @PostMapping("/logout")
    public JsonResult logout(HttpServletRequest request, HttpServletResponse response) throws Exception{
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated() || subject.getPrincipals() != null){
            subject.logout();
        }

        return new JsonResult(Status.OK,  new String[]{"退出登录成功"});
    }

}
