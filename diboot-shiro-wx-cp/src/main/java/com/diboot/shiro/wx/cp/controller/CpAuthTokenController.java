package com.diboot.shiro.wx.cp.controller;

import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Status;
import com.diboot.shiro.config.AuthType;
import com.diboot.shiro.entity.TokenAccountInfo;
import com.diboot.shiro.jwt.BaseJwtAuthenticationToken;
import com.diboot.shiro.service.AuthWayService;
import com.diboot.shiro.util.JwtHelper;
import com.diboot.shiro.wx.cp.config.WxCpConfig;
import com.diboot.shiro.wx.cp.enums.UserTypeEnum;
import com.diboot.shiro.wx.cp.service.impl.WxCpServiceExtImpl;
import me.chanjar.weixin.common.api.WxConsts;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/cpToken")
public class CpAuthTokenController {

    private static final Logger logger = LoggerFactory.getLogger(CpAuthTokenController.class);

    @Autowired
    private Map<String, AuthWayService> authWayServiceMap;

    @Autowired
    private WxCpServiceExtImpl wxCpService;

    /**
     * 企业微信创建OAuth认证链接(为了获取授权所需code)
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping("/buildOAuthUrl")
    public JsonResult buildOAuthUrl4cp(@RequestParam("url") String url,@RequestParam("agentId") Integer agentId, HttpServletRequest request) throws Exception{
        if (V.isEmpty(url)){
            return new JsonResult(Status.FAIL_OPERATION, new String[]{"url为空，获取OAuth链接失败"});
        }
        wxCpService = (WxCpServiceExtImpl)WxCpConfig.getCpService(agentId);

        String oauthUrl = wxCpService.getOauth2Service().buildAuthorizationUrl(url, null, WxConsts.OAuth2Scope.SNSAPI_PRIVATEINFO);
        return new JsonResult(Status.OK, oauthUrl, new String[]{"获取OAuth链接成功"});
    }

    /**
     * 企业微信的回调授权登录认证
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/apply")
    public JsonResult applyTokenByOAuth2cp(@RequestParam("code") String code, HttpServletRequest request) throws Exception{
        String userId = "";
        if (JwtHelper.isRequestTokenEffective(request)){
            TokenAccountInfo account = JwtHelper.getAccountFromToken(JwtHelper.getRequestToken(request));
            if (V.isEmpty(account)){
                // 如果有code并且token已过期，则使用code获取userId
                if (V.isEmpty(code)){
                    return new JsonResult(Status.FAIL_INVALID_TOKEN, new String[]{"token已过期"});
                }
            } else {
                userId = account.getAccount();
            }
        }

        // 如果userId没有通过token获取到，则通过code获取
        if (V.isEmpty(userId)){
            if (V.isEmpty(code)){
                // 如果没有code参数，则返回提示信息
                return new JsonResult(Status.FAIL_INVALID_TOKEN, new String[]{"请重新进入页面"});
            }
            String[] res = wxCpService.getOauth2Service().getUserInfo(code);
            userId = res[0];
        }

        // 如果没有获取到userId，则返回提示信息
        if (V.isEmpty(userId)){
            return new JsonResult(Status.FAIL_INVALID_TOKEN, new String[]{"获取信息失败"});
        }

        // 设置token
        BaseJwtAuthenticationToken authToken = new BaseJwtAuthenticationToken(authWayServiceMap, userId, AuthType.WX_CP, UserTypeEnum.WX_CP_USER);
        // 获取当前的Subject
        Subject subject = SecurityUtils.getSubject();
        String token = null;
        String errorMsg = null;

        try {
            subject.login(authToken);
            //验证是否登录成功
            if(subject.isAuthenticated()){
                token = (String)authToken.getCredentials();
                logger.debug("userId[" + userId + "]申请token成功！authtoken="+token);
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

}
