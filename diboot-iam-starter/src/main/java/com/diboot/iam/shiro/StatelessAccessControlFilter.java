/*
 * Copyright (c) 2015-2029, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.iam.shiro;

import com.diboot.core.util.JSON;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Status;
import com.diboot.iam.config.Cons;
import com.diboot.iam.util.IamSecurityUtils;
import com.diboot.iam.util.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 无状态的访问控制过滤器
 * @author JerryMa
 * @version v2.6.0
 * @date 2022/4/26
 * Copyright © diboot.com
 */
@Slf4j
public class StatelessAccessControlFilter extends BasicHttpAuthenticationFilter {

    /**
     * 判断是否登录
     *
     * 返回true则直接进入控制器
     * @param request
     * @param response
     * @param mappedValue
     * @return
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        // 从header获取Token
        String currentToken = TokenUtils.getRequestToken(httpRequest);
        if (V.isEmpty(currentToken)) {
            log.debug("token: {} 验证失败, uri={}", currentToken, httpRequest.getRequestURI());
            return false;
        }
        log.debug("token: {} 验证通过", currentToken);
        String cachedUserInfo = TokenUtils.getCachedUserInfoStr(currentToken);
        if(IamSecurityUtils.getSubject().isAuthenticated() == false && cachedUserInfo != null){
            IamAuthToken authToken = new IamAuthToken(cachedUserInfo);
            authToken.setAuthtoken(currentToken);
            authToken.setValidPassword(false);
            IamSecurityUtils.getSubject().login(authToken);
            log.debug("token: {} 保活完成, uri={}", currentToken, httpRequest.getRequestURI());
        }
        // 如果临近过期，则生成新的token返回
        TokenUtils.responseNewTokenIfRequired(response, cachedUserInfo);
        return true;
    }

    /**
     * 没有登录的情况下会走此方法
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        log.debug("Token认证失败： onAccessDenied");
        JsonResult jsonResult = new JsonResult(Status.FAIL_INVALID_TOKEN);
        this.responseJson((HttpServletResponse)response, jsonResult);
        return false;
    }

    /***
     * 返回json格式错误信息
     * @param response
     * @param jsonResult
     */
    protected void responseJson(HttpServletResponse response, JsonResult jsonResult){
        // 处理异步请求
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(Cons.CHARSET_UTF8);
        try (PrintWriter pw = response.getWriter()){
            pw.write(JSON.stringify(jsonResult));
            pw.flush();
        }
        catch (IOException e) {
            log.error("处理异步请求异常", e);
        }
    }

}
