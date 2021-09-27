package com.diboot.mobile.service;

/**
 * @author : uu
 * @version : v1.0
 * @Date 2021/9/26  18:10
 */
public interface WxMpMemberAuthService {

    /**
     * 构建认证URL
     *
     * @param url
     * @return
     * @throws Exception
     */
    String buildOAuthUrl4mp(String url) throws Exception;


    /**
     * 申请登陆
     *
     * @param code
     * @param state
     * @return
     * @throws Exception
     */
    String applyLogin(String code, String state) throws Exception;
}
