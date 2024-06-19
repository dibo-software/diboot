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
package com.diboot.ai.models.wenxin;

import com.diboot.ai.common.AiMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 百度千帆请求
 *
 * @author : uu
 * @version : v3.4
 * @Date 2024/5/7
 */
@Getter
@Setter
@Accessors(chain = true)
public class WenXinChatRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -4159393465777729774L;
    /**
     * 聊天上下文信息
     */
    private List<AiMessage> messages;

    /**
     * 用于控制随机性和多样性的程度。
     * <p>
     * 高的temperature值会降低概率分布的峰值，使得更多的低概率词被选择，生成结果更加多样化；而较低的temperature值则会增强概率分布的峰值，使得高概率词更容易被选择，生成结果更加确定
     */
    private Double temperature = 0.8;

    /**
     * 生成时，核采样方法的概率阈值
     */
    @JsonProperty("top_p")
    private Double topP = 0.8;

    /**
     * 是否以流式接口的形式返回数据
     */
    private Boolean stream = true;

    /**
     * 是否强制关闭实时搜索功能，默认false，表示不关闭
     */
    @JsonProperty("disable_search")
    private Boolean disableSearch = false;

    /**
     * 是否开启上角标返回，说明：
     * <p>
     * （1）开启后，有概率触发搜索溯源信息search_info，search_info内容见响应参数介绍
     * （2）默认false，不开启
     */
    @JsonProperty("enable_citation")
    private Boolean enableCitation = false;
}
