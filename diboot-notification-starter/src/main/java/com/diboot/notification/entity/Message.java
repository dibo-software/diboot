/*
 * Copyright (c) 2015-2021, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.notification.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diboot.core.entity.BaseEntity;
import com.diboot.core.util.D;
import com.diboot.core.util.JSON;
import com.diboot.core.util.V;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


/**
 * 消息 Entity定义
 *
 * @author : uu
 * @version : v2.0
 * @Date 2021/2/25  09:39
 * @Copyright © diboot.com
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("dbt_message")
public class Message extends BaseEntity<String> {

    private static final long serialVersionUID = -2648761257152687435L;

    /**
     * channel字段的关联字典
     */
    public static final String DICT_MESSAGE_CHANNEL = "MESSAGE_CHANNEL";

    /**
     * status字段的关联字典
     */
    public static final String DICT_MESSAGE_STATUS = "MESSAGE_STATUS";


    /**
     * 多个接受人（如果邮件需要发送多个，可以借助这个字段）
     * {@link Message#receiver}只能设置一个
     */
    public final static String RECEIVERS = "receivers";

    /**
     * 抄送人
     */
    public final static String CC_EMAILS = "ccEmails";

    /**
     * 隐秘抄送人
     */
    public final static String BCC_EMAILS = "bccEmails";

    /**
     * 附件
     */
    public final static String ATTACHMENTS = "attachments";

    /**
     * 变量
     */
    public final static String VARIABLES = "variables";


    /**
     * 租户id
     */
    @TableField()
    private String tenantId;

    /**
     * 应用模块
     */
    @Length(max = 50, message = "{validation.message.appModule.Length.message}")
    @TableField()
    private String appModule;

    /**
     * 信息模版id
     */
    @TableField()
    private String templateId;

    /**
     * 信息模板code
     */
    @TableField(exist = false)
    private String templateCode;

    /**
     * 业务类型
     */
    @NotNull(message = "{validation.message.businessType.NotNull.message}")
    @Length(max = 32, message = "{validation.message.businessType.Length.message}")
    @TableField()
    private String businessType;

    /**
     * 业务标识
     */
    @NotNull(message = "{validation.message.businessCode.NotNull.message}")
    @Length(max = 32, message = "{validation.message.businessCode.Length.message}")
    @TableField()
    private String businessCode;

    /**
     * 标题
     */
    @Length(max = 100, message = "{validation.message.title.Length.message}")
    @TableField()
    private String title;

    /**
     * 内容
     */
    @NotNull(message = "{validation.message.content.NotNull.message}")
    @TableField()
    private String content;

    /**
     * 发送方
     */
    @NotNull(message = "{validation.message.sender.NotNull.message}")
    @Length(max = 100, message = "{validation.message.sender.Length.message}")
    @TableField()
    private String sender;

    /**
     * 接收方
     */
    @NotNull(message = "{validation.message.receiver.NotNull.message}")
    @Length(max = 100, message = "{validation.message.receiver.Length.message}")
    @TableField()
    private String receiver;

    /**
     * 发送通道
     */
    @NotNull(message = "{validation.message.channel.NotNull.message}")
    @Length(max = 30, message = "{validation.message.channel.Length.message}")
    @TableField()
    private String channel;

    /**
     * 消息状态
     */
    @NotNull(message = "{validation.message.status.NotNull.message}")
    @Length(max = 30, message = "{validation.message.status.Length.message}")
    @TableField()
    private String status;

    /**
     * 发送结果
     */
    @TableField()
    private String result;

    /**
     * 定时发送时间
     */
    @JsonFormat(pattern = D.FORMAT_DATETIME_Y4MDHM)
    @TableField()
    private LocalDateTime scheduleTime;

    /**
     * 扩展数据
     */
    @Length(max = 500, message = "{validation.message.extData.Length.message}")
    @TableField()
    private String extData;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    /**
     * 扩展字段Map
     */
    @TableField(exist = false)
    private Map<String, Object> extDataMap;

    public Map<String, Object> getExtDataMap() {
        return V.isEmpty(this.extData) ? new HashMap<>(16) : JSON.toMap(this.extData);
    }

    public void setExtDataMap(Map<String, Object> extDataMap) {
        if (V.isEmpty(extDataMap)) {
            extDataMap = new HashMap<>(16);
        }
        this.extDataMap = extDataMap;
        this.extData = JSON.stringify(extDataMap);
    }

    public Message setExtDataAttribute(String key, String value) {
        if (V.isEmpty(extDataMap)) {
            extDataMap = new HashMap<>(16);
        }
        this.extDataMap.put(key, value);
        this.extData = JSON.stringify(extDataMap);
        return this;
    }

    /**
     * 获取多个接受者
     *
     * @return
     */
    public String[] getReceivers() {
        if (V.isEmpty(extDataMap) || V.isEmpty(extDataMap.get(RECEIVERS))) {
            return new String[]{};
        }
        String receivers = (String) extDataMap.get(RECEIVERS);
        return receivers.split(",");
    }

    /**
     * 获取抄送人
     *
     * @return
     */
    public String[] getCcEmails() {
        if (V.isEmpty(extDataMap) || V.isEmpty(extDataMap.get(CC_EMAILS))) {
            return new String[]{};
        }
        String ccEmails = (String) extDataMap.get(CC_EMAILS);
        return ccEmails.split(",");
    }

    /**
     * 获取隐秘抄送人
     *
     * @return
     */
    public String[] getBccEmails() {
        if (V.isEmpty(extDataMap) || V.isEmpty(extDataMap.get(BCC_EMAILS))) {
            return new String[]{};
        }
        String bccEmails = (String) extDataMap.get(BCC_EMAILS);
        return bccEmails.split(",");
    }

    /**
     * 获取附件
     *
     * @return
     */
    public String[] getAttachments() {
        if (V.isEmpty(extDataMap) || V.isEmpty(extDataMap.get(ATTACHMENTS))) {
            return new String[]{};
        }
        String attachments = (String) extDataMap.get(ATTACHMENTS);
        return attachments.split(",");
    }

    /**
     * 获取当前消息的变量json
     *
     * @return
     */
    public String getVariables() {
        if (V.isEmpty(extDataMap) || V.isEmpty(extDataMap.get(VARIABLES))) {
            return "";
        }
        Object variables =  extDataMap.get(VARIABLES);
        return JSON.stringify(variables);
    }

    /**
     * 是否有关联模板
     *
     * @return
     */
    public boolean hasTemplate() {
        return V.notEmpty(this.templateId) || V.notEmpty(this.templateCode);
    }
}
