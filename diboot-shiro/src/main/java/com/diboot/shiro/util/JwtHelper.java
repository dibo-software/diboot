package com.diboot.shiro.util;

import com.diboot.core.config.BaseConfig;
import com.diboot.core.util.V;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Token相关操作类
 * @author Mazc@dibo.ltd
 * @version 2017/9/2
 *
 */
public class JwtHelper {
    private static final Logger logger = LoggerFactory.getLogger(JwtHelper.class);

    private static final String ISSUER = V.notEmpty(BaseConfig.getProperty("diboot.shiro.jwt.issuer")) ? BaseConfig.getProperty("diboot.shiro.jwt.issuer") : "diboot.com";
    private static final String AUTH_HEADER = V.notEmpty(BaseConfig.getProperty("diboot.shiro.jwt.auth.header.key")) ? BaseConfig.getProperty("diboot.shiro.jwt.auth.header.key") : "authtoken";
    private static final String TOKEN_PREFIX = V.notEmpty(BaseConfig.getProperty("diboot.shiro.jwt.token.prefix")) ? BaseConfig.getProperty("diboot.shiro.jwt.token.prefix") : "Bearer ";
    public static final String SIGN_KEY = V.notEmpty(BaseConfig.getProperty("diboot.shiro.jwt.signkey"))? BaseConfig.getProperty("diboot.shiro.jwt.signkey") : "Dibo2016Mazc";

    // 默认过期时间 2小时
    public static final int EXPIRES_IN_MINUTES = V.notEmpty(BaseConfig.getProperty("diboot.shiro.jwt.token.expires.hours")) ? Integer.valueOf(BaseConfig.getProperty("diboot.shiro.jwt.token.expires.hours")) * 60 : 2 * 60;
    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;

    /***
     * 从token中获取用户名
     * @param token
     * @return
     */
    public static String getAccountFromToken(String token){
        return getAccountFromToken(token, SIGN_KEY);
    }

    /***
     * 从token中获取用户名
     * @param token
     * @return
     */
    public static String getAccountFromToken(String token, String key){
        String username;
        try {
            Claims claims = getClaimsFromToken(token, key);
            // 校验过期时间
            if(claims.getExpiration().getTime() >= System.currentTimeMillis()){
                username = claims.getSubject();
                logger.debug("token有效，username=" + username);
            }
            else{
                logger.warn("token已过期:" + token);
                username = null;
            }
        }
        catch (Exception e) {
            logger.warn("解析token异常，无效的token:" + token);
            username = null;
        }
        return username;
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
                return authHeader.substring(7);
            }
            return authHeader.trim();
        }
        return null;
    }

    /***
     * 请求头的token是否处于有效期限内
     */
    public static boolean isRequestTokenEffective(HttpServletRequest request){
        String authToken = getRequestToken(request);
        if(V.notEmpty(authToken)){
            String account = getAccountFromToken(authToken);
            return V.notEmpty(account);
        }
        return false;
    }

    /***
     * 生成Token
     * @param username
     * @param signKey
     * @return
     */
    public static String generateToken(String username, String signKey) {
        return generateToken(username, ISSUER, SIGNATURE_ALGORITHM, signKey, EXPIRES_IN_MINUTES);
    }

    /***
     * 生成Token
     * @param username
     * @param signKey
     * @param expiresInMinutes
     * @return
     */
    public static String generateToken(String username, String signKey, long expiresInMinutes) {
        return generateToken(username, ISSUER, SIGNATURE_ALGORITHM, signKey, expiresInMinutes);
    }

    /***
     * 生成token
     * @param user
     * @param issuer
     * @param signAlgorithm
     * @param signKey
     * @param expiresInMinutes
     * @return
     */
    public static String generateToken(String user, String issuer, SignatureAlgorithm signAlgorithm, String signKey, long expiresInMinutes) {
        Date currentTime = generateCurrentDate();
        Date expiration = generateExpirationDate(currentTime, expiresInMinutes);
        String jwsToken = Jwts.builder()
                .setIssuer(issuer)
                .setSubject(user)
                .setIssuedAt(currentTime)
                .setExpiration(expiration)
                .signWith(signAlgorithm, signKey)
                .compact();
        return jwsToken;
    }

    /***
     * 获取Token
     * @param token
     * @return
     */
    public static Claims getClaimsFromToken(String token, String key) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(key)
                    .parseClaimsJws(token).getBody();
        }
        catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    /***
     * 校验Token
     * @param authToken
     * @param key
     * @return
     */
    public static boolean isValidToken(String authToken, String key, String expectedUsername) {
        try {
            final Claims claims = getClaimsFromToken(authToken, key);
            if(claims != null){
                String authUsername = claims.getSubject();
                // 校验用户名
                if(authUsername != null && authUsername.equals(expectedUsername)){
                    // 校验过期时间
                    return claims.getExpiration().getTime() >= System.currentTimeMillis();
                }
            }
        }
        catch (Exception e) {
            logger.warn("校验token异常", e);
        }
        return false;
    }

    /***
     * 生成当前时间戳
     * @return
     */
    public static Date generateCurrentDate() {
        return new Date(System.currentTimeMillis());
    }

    /***
     * 生成过期时间戳
     * @return
     */
    public static Date generateExpirationDate(Date currentTime, long expiresInMinutes) {
        return new Date(currentTime.getTime() + (expiresInMinutes*60000));
    }
}
