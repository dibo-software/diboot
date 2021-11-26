package com.diboot.mobile.service;

import com.diboot.mobile.entity.IamMember;

/**
 * 微信公众号Service
 *
 * @author : uu
 * @version : v1.0
 * @Date 2021/9/26  18:10
 */
public interface WxMpAuthService {

    /**
     * 构建认证URL
     *
     * @param url
     * @return
     * @throws Exception
     */
    String buildOAuthUrl4mp(String url) throws Exception;


    /**
     * 用户绑定公众号
     *
     * 已经绑定提示已经绑定
     * 未绑定，IamMember自动创建，绑定IamUser
     * @param code
     * @param state
     * @return
     * @throws Exception
     */
    IamMember bindWxMp(String code, String state) throws Exception;


    /**
     * 申请token
     *
     * 用户不存在时，自动创建
     * @param code
     * @param state
     * @return
     * @throws Exception
     */
    String applyToken(String code, String state) throws Exception;
}
