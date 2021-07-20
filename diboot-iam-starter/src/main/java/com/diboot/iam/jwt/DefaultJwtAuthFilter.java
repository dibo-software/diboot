/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.iam.jwt;

import com.diboot.core.util.JSON;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Status;
import com.diboot.iam.config.Cons;
import com.diboot.iam.util.JwtUtils;
import io.jsonwebtoken.Claims;
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
 * JWT 认证过滤器
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
@Slf4j
public class DefaultJwtAuthFilter extends BasicHttpAuthenticationFilter {

    /**
     * Shiro权限拦截核心方法 使用JWT进行认证 返回true允许访问
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        // 从header获取Token
        Claims claims = JwtUtils.getClaimsFromRequest(httpRequest);
        if (V.isEmpty(claims)) {
            return false;
        }
        //解密是否成功
        if(V.notEmpty(claims.getSubject())){
            log.debug("Token验证成功！account={}, url={}", claims.getSubject(), httpRequest.getRequestURI());
            // 如果临近过期，则生成新的token返回
            String refreshToken = JwtUtils.generateNewTokenIfRequired(claims);
            if(refreshToken != null){
                // 写入response header中
                JwtUtils.addTokenToResponseHeader((HttpServletResponse) response, refreshToken);
            }
            return true;
        }
        log.debug("Token验证失败！url=" + httpRequest.getRequestURI());
        return false;
    }

    /**
     * 当访问拒绝时是否已经处理了；如果返回true表示需要继续处理；如果返回false表示该拦截器实例已经处理
     * @param
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        log.debug("Token认证失败： onAccessDenied。url={}", httpRequest.getRequestURI());
        JsonResult jsonResult = new JsonResult(Status.FAIL_INVALID_TOKEN);
        this.responseJson((HttpServletResponse) response, jsonResult);
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
