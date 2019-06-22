package com.diboot.shiro.wx.mp.service;

import com.diboot.core.config.BaseConfig;
import com.diboot.core.util.V;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class WxMpServiceExt extends WxMpServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(WxMpServiceExt.class);

    private WxMpMessageRouter wxMpMessageRouter;

    private static final String appId = BaseConfig.getProperty("wechat.appId");
    private static final String secret = BaseConfig.getProperty("wechat.secret");
    private static final String token = BaseConfig.getProperty("wechat.token");
    private static final String aesKey = BaseConfig.getProperty("wechat.aesKey");

    @PostConstruct
    public void init(){
        WxMpInMemoryConfigStorage inMemoryConfigStorage = new WxMpInMemoryConfigStorage();

        if (V.isEmpty(appId) || V.isEmpty(secret)){
            logger.warn("服务号相关的appid或secret为空，请检查application.properties配置文件");
        }

        inMemoryConfigStorage.setAppId(appId);
        inMemoryConfigStorage.setSecret(secret);
        inMemoryConfigStorage.setToken(token);
        inMemoryConfigStorage.setAesKey(aesKey);

        setWxMpConfigStorage(inMemoryConfigStorage);

        wxMpMessageRouter = new WxMpMessageRouter(this);
        wxMpMessageRouter.rule().handler(new WxMpMessageHandler() {
            @Override
            public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
                logger.info("\n接收到 {} 公众号请求消息，内容：{}", wxMpService.getWxMpConfigStorage().getAppId(), wxMpXmlMessage);
                return null;
            }
        }).next();
    }

    public WxMpMessageRouter getWxMpMessageRouter(){
        return wxMpMessageRouter;
    }

}
