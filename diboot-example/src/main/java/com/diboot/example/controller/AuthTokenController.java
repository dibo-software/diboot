package com.diboot.example.controller;

import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Status;
import com.diboot.shiro.BaseJwtAuthenticationToken;
import com.diboot.shiro.config.AuthType;
import com.diboot.shiro.entity.SysUser;
import com.diboot.shiro.service.AuthWayService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/token")
public class AuthTokenController {

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenController.class);

    @Autowired
    private Map<String, AuthWayService> authWayServiceMap;

    /***
     * 用户名密码登录接口
     * @param sysUser
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @PostMapping("/login")
    public JsonResult login(@ModelAttribute SysUser sysUser, HttpServletRequest request, HttpServletResponse response) throws Exception{
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

    @PostMapping("/logout")
    public JsonResult logout(HttpServletRequest request, HttpServletResponse response) throws Exception{
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated() || subject.getPrincipals() != null){
            subject.logout();
        }

        return new JsonResult(Status.OK,  new String[]{"退出登录成功"});
    }
}
