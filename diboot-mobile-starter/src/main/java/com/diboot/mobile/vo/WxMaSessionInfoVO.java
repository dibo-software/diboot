package com.diboot.mobile.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * code换取session_key接口的响应
 * 文档地址：https://mp.weixin.qq.com/debug/wxadoc/dev/api/api-login.html#wxloginobject
 * 微信返回报文：{"session_key":"nzoqhc3OnwHzeTxJs+inbQ==","openid":"oVBkZ0aYgDMDIywRdgPW8-joxXc4"}
 *
 * @author : uu
 * @version : v2.3.1
 * @Date 2021/9/27  11:17
 */
@Setter
@Getter
@Accessors(chain = true)
public class WxMaSessionInfoVO implements Serializable {
    private static final long serialVersionUID = 5085761592400763401L;

    /**
     * sessionKey
     */
    private String sessionKey;
    /**
     * openid
     */
    private String openid;
    /**
     * unionid
     */
    private String unionid;
}
