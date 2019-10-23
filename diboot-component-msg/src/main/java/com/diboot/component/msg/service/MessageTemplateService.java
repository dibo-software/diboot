package com.diboot.component.msg.service;

import com.diboot.component.msg.entity.MessageTemplate;
import com.diboot.core.service.BaseService;

/*
* 消息模板相关service
* @author:wangyl
* */
public interface MessageTemplateService extends BaseService<MessageTemplate> {

    /**
     * 根据code获取唯一的消息模板
     * @param code
     * @return
     */
    MessageTemplate getMessageTemplate(String code);

    /**
     * 根据code获取消息模板变量
     * @param code
     * @return
     */
    String[] getTemplateVaribles(String code);

}
