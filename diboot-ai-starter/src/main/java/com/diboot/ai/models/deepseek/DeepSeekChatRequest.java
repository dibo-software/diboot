package com.diboot.ai.models.deepseek;

import com.diboot.ai.common.AiMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * DeepSeek 对话请求对象
 */
@Getter
@Setter
@Accessors(chain = true)
public class DeepSeekChatRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -7235485584499603198L;

    /**
     * 指定用于对话的模型: 默认使用MOONSHOT_V1_8K
     */
    private String model = DeepSeekEnum.Model.DEEPSEEK_CHAT.getCode();

    /**
     * temperature 参数默认为 1.0。
     *
     * <table border="10">
     * <tr>
     * <th>场景</th>
     * <th>温度</th>
     * </tr>
     * <tr>
     * <td>代码生成/数学解题</td>
     * <td>0.0</td>
     * </tr>
     * <td>数据抽取/分析</td>
     * <td>1.0</td>
     * </tr>
     * <tr>
     * <td>通用对话</td>
     * <td>1.3</td>
     * </tr>
     * <tr>
     * <td>翻译</td>
     * <td>1.3</td>
     * </tr>
     * <tr>
     * <td>创意类写作/诗歌创作</td>
     * <td>1.5</td>
     * </tr>
     * </table>
     */
    private Double temperature = 1.0;

    /**
     * 聊天上下文信息
     */
    private List<AiMessage> messages;


    /**
     * 是否以流式接口的形式返回数据
     */
    private Boolean stream = true;
}
