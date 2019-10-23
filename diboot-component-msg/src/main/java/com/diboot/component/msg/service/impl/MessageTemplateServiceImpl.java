package com.diboot.component.msg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.diboot.component.msg.entity.MessageTemplate;
import com.diboot.component.msg.mapper.MessageTemplateMapper;
import com.diboot.component.msg.service.MessageTemplateService;
import com.diboot.core.entity.Dictionary;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * 消息模板相关service
 * @author:wangyl
 * */
@Service
@Slf4j
public class MessageTemplateServiceImpl extends BaseServiceImpl<MessageTemplateMapper, MessageTemplate> implements MessageTemplateService {
    private static final Logger logger = LoggerFactory.getLogger(MessageTemplateServiceImpl.class);

    @Autowired
    private DictionaryService dictionaryService;

    @Override
    public MessageTemplate getMessageTemplate(String code) {
        LambdaQueryWrapper<MessageTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageTemplate::getCode, code);
        List<MessageTemplate> list =  this.getEntityList(wrapper);
        return V.notEmpty(list)? list.get(0) : null;
    }

    @Override
    public String[] getTemplateVaribles(String code) {
        LambdaQueryWrapper<Dictionary> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(Dictionary::getParentId, 0)
                .eq(Dictionary::getItemName, code)
                .eq(Dictionary::getType, MessageTemplate.METADATA_TYPE.MSG_TEMP_VARIBLES.name());
        List<Dictionary> list = dictionaryService.getEntityList(wrapper);
        if (V.isEmpty(list)){
            return null;
        }
        return S.split(list.get(0).getItemValue());
    }
}
