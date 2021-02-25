package com.diboot.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.example.demo.entity.MessageTemplate;
import com.example.demo.mapper.MessageTemplateMapper;
import com.example.demo.service.MessageTemplateService;
import com.example.demo.strategy.template.TemplateUtils;
import com.example.demo.vo.TemplateStrategyVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* 消息模版相关Service实现
* @author MyName
* @version 1.0
* @date 2021-02-18
 * Copyright © MyCompany
*/
@Service
@Slf4j
public class MessageTemplateServiceImpl extends BaseCustomServiceImpl<MessageTemplateMapper, MessageTemplate> implements MessageTemplateService {

    @Override
    public List<TemplateStrategyVO> getTemplateStrategy() throws Exception{
        return TemplateUtils.loadAllTemplateStrategyConfig();
    }

    @Override
    public boolean existCode(Long id, String code) throws Exception {
        if (V.isEmpty(code)) {
            return false;
        }
        LambdaQueryWrapper<MessageTemplate> wrapper = Wrappers.<MessageTemplate>lambdaQuery()
                .eq(MessageTemplate::getCode, code);
        // 如果id存在，那么需要排除当前id进行查询
        if (V.notEmpty(id)) {
            wrapper.ne(MessageTemplate::getCode, id);
        }
        if (V.notEmpty(getEntityList(wrapper))) {
            throw new BusinessException(Status.FAIL_OPERATION, "模版编码[" + code + "]已存在");
        }
        return false;
    }
}
