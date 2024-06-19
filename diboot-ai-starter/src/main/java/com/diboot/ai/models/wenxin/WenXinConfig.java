package com.diboot.ai.models.wenxin;

import com.diboot.ai.models.AiConfig;
import lombok.Getter;
import lombok.Setter;

/**
 * 百度 千帆配置
 * @author : uu
 * @version : v3.4
 * @Date 2024/5/7
 */
@Getter
@Setter
public class WenXinConfig extends AiConfig {

    /**
     * 对话 APi
     */
    private String chatApi = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/yi_34b_chat";

    /**
     * Secret key
     */
    private String secretKey;
}
