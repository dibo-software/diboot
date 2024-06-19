package com.diboot.ai.models.kimi;

import com.diboot.ai.common.AiMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Kimi 对话请求对象
 * @author JerryMa
 * @version v3.4.0
 * @date 2024/5/7
 */
@Getter @Setter @Accessors(chain = true)
public class KimiChatRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -2428161640089613168L;

    /**
     * 指定用于对话的模型: 默认使用MOONSHOT_V1_8K
     */
    private String model = KimiEnum.Model.MOONSHOT_V1_8K.getCode();

    /**
     * 包含迄今为止对话的消息列表
     */
    private List<AiMessage> messages;

    /**
     * 使用什么采样温度，介于 0 和 1 之间。较高的值（如 0.7）将使输出更加随机，而较低的值（如 0.2）将使其更加集中和确定性
     */
    private Float temperature = 0.3f;

    /**
     * 是否流式返回
     */
    private boolean stream = true;

    /**
     * 聊天完成时生成的最大 token 数。如果到生成了最大 token 数个结果仍然没有结束，finish reason 会是 "length", 否则会是 "stop"
     */
    @JsonProperty("max_tokens")
    private Integer maxTokens;

    /**
     * 另一种采样方法，即模型考虑概率质量为 top_p 的标记的结果。因此，0.1 意味着只考虑概率质量最高的 10% 的标记。一般情况下，我们建议改变这一点或温度，但不建议 同时改变
     */
    @JsonProperty("top_p")
    private Float topP = 1.0f;

    /**
     * 为每条输入消息生成多少个结果
     */
    private Integer n = 1;

    /**
     * 存在惩罚，介于-2.0到2.0之间的数字。正值会根据新生成的词汇是否出现在文本中来进行惩罚，增加模型讨论新话题的可能性
     */
    @JsonProperty("presence_penalty")
    private Float presencePenalty;

    /**
     * 频率惩罚，介于-2.0到2.0之间的数字。正值会根据新生成的词汇在文本中现有的频率来进行惩罚，减少模型一字不差重复同样话语的可能性
     */
    @JsonProperty("frequency_penalty")
    private Float frequencyPenalty;

    /**
     * 停止词，当全匹配这个（组）词后会停止输出，这个（组）词本身不会输出。最多不能超过 5 个字符串，每个字符串不得超过 32 字节
     */
    private String stop;

}
