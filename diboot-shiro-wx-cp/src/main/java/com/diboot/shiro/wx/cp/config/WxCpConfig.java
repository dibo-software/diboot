package com.diboot.shiro.wx.cp.config;

import com.diboot.core.config.BaseConfig;
import com.diboot.core.util.JSON;
import com.diboot.core.util.V;
import com.google.common.collect.Maps;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.config.WxCpInMemoryConfigStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Configuration
public class WxCpConfig {
    private static final Logger logger = LoggerFactory.getLogger(WxCpConfig.class);

    private static final String WECHAT_CP_CORPID = BaseConfig.getProperty("wechat.cp.corpId");
    private static final String WECHAT_CP_APPS = BaseConfig.getProperty("wechat.cp.apps");
    private final String APPS = "apps";
    private final String AGENT_ID = "agentId";
    private final String SECRET = "secret";

    private static Map<Integer, WxCpService> cpServices = Maps.newHashMap();

    public static WxCpService getCpService(Integer agentId) {
        return cpServices.get(agentId);
    }

    @PostConstruct
    public void initServices() {
        Map appMap = JSON.toMap(WECHAT_CP_APPS);
        List<Map> appMapList = null;
        if(V.notEmpty(appMap)){
            appMapList = (List)appMap.get(APPS);
        }

        if(V.notEmpty(appMapList)){
            for(Map app : appMapList){
                WxCpService service = new WxCpServiceImpl();
                WxCpInMemoryConfigStorage inMemoryConfigStorage = new WxCpInMemoryConfigStorage();
                Integer agentId = Integer.parseInt(app.get(AGENT_ID).toString());
                String secret = app.get(SECRET).toString();

                inMemoryConfigStorage.setAgentId(agentId);
                inMemoryConfigStorage.setCorpSecret(secret);
                inMemoryConfigStorage.setCorpId(WECHAT_CP_CORPID.trim());
                service.setWxCpConfigStorage(inMemoryConfigStorage);

                cpServices.put(agentId, service);
            }

        }else{
            logger.warn("暂无企业微信配置信息");
        }

    }

}
