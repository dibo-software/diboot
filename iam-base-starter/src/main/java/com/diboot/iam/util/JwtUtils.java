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
package com.diboot.iam.util;

import com.diboot.core.config.BaseConfig;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.iam.config.Cons;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Token相关操作类
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
public class JwtUtils {
    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String AUTH_HEADER = getConfigValue("diboot.iam.jwt-header-key", "authtoken");
    public static final String SIGN_KEY = getConfigValue("diboot.iam.jwt-signkey", "Diboot");
    // 默认过期时间 2小时
    public static final int EXPIRES_IN_MINUTES = getConfigIntValue("diboot.iam.jwt-token-expires-minutes", 1*60);

    // 默认加密算法
    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;

    /***
     * 从token中获取用户名 + 用户类型
     * <br>
     * 返回格式：
     * <code>authAccount,expiresInMinutes</code>
     * @param request
     * @return
     */
    public static Claims getClaimsFromRequest(HttpServletRequest request){
        String authtoken = getRequestToken(request);
        if (V.isEmpty(authtoken)) {
            log.warn("Token为空！url={}", request.getRequestURL());
            return null;
        }
        try {
            return Jwts.parser().setSigningKey(SIGN_KEY).parseClaimsJws(authtoken).getBody();
        }
        catch (ExpiredJwtException e) {
            log.info("token已过期:{}", authtoken);
        }
        catch (Exception e){
            log.warn("token解析异常", e);
        }
        return null;
    }

    /***
     * 从请求头中获取客户端发来的token
     * @param request
     * @return
     */
    public static String getRequestToken(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTH_HEADER);
        if(authHeader != null){
            if(authHeader.startsWith(TOKEN_PREFIX)){
                return authHeader.substring(TOKEN_PREFIX.length());
            }
            return authHeader.trim();
        }
        return null;
    }


    /***
     * 将刷新的token放入response header
     * @param response
     * @return
     */
    public static void addTokenToResponseHeader(HttpServletResponse response, String newToken) {
        response.setHeader(AUTH_HEADER, newToken);
    }

    /***
     * 生成Token
     * @param accountInfo
     * @param expiresInMinutes
     * @return
     */
    public static String generateToken(String accountInfo, long expiresInMinutes){
        Date expiration = expirationDate(expiresInMinutes);
        String jwsToken = Jwts.builder()
                .setSubject(accountInfo)
                .setIssuedAt(now())
                .setExpiration(expiration)
                .signWith(SIGNATURE_ALGORITHM, SIGN_KEY)
                .compact();
        return jwsToken;
    }

    /**
     * 临近过期时生成新的token
     * @param claims
     * @return
     */
    public static String generateNewTokenIfRequired(Claims claims){
        // 当前token是否临近过期
        long current = System.currentTimeMillis();
        long remaining = claims.getExpiration().getTime() - current;
        if(remaining > 0){
            long elapsed = current - claims.getIssuedAt().getTime();
            // 小于1/4 则更新
            if((elapsed / remaining) >= 3){
                // 更新token
                int expiresInMinutes = Integer.parseInt(S.substringAfter(claims.getSubject(), Cons.SEPARATOR_COMMA));
                return JwtUtils.generateToken(claims.getSubject(), expiresInMinutes);
            }
        }
        return null;
    }

    /**
     * 当前时间
     * @return
     */
    private static Date now(){
        return new Date(System.currentTimeMillis());
    }

    /***
     * 生成过期时间戳
     * @return
     */
    private static Date expirationDate(long expiresInMinutes) {
        return new Date(System.currentTimeMillis() + (expiresInMinutes*60000));
    }

    /**
     * 获取配置参数值
     * @return
     */
    private static String getConfigValue(String key, String defaultValue){
        String value = BaseConfig.getProperty(key);
        return value != null? value : defaultValue;
    }


    /**
     * 获取配置参数值
     * @return
     */
    private static int getConfigIntValue(String key, int defaultValue){
        String value = BaseConfig.getProperty(key);
        return value != null? Integer.parseInt(value) : defaultValue;
    }

}
