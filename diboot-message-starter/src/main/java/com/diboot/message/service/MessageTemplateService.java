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
package com.diboot.message.service;

import com.diboot.core.service.BaseService;
import com.diboot.message.entity.MessageTemplate;

import java.util.List;

/**
 * 消息模版相关Service
 *
 * @author : uu
 * @version : v2.0
 * @Date 2021/2/25  09:39
 * @Copyright © diboot.com
 */
public interface MessageTemplateService extends BaseService<MessageTemplate> {

    /**
     * 获取系统提供的变量
     *
     * @return
     * @throws Exception
     */
    List<String> getTemplateVariableList() ;

    /**
     * 检查是否有重复的code
     *
     * @param id
     * @param code
     * @return
     * @throws Exception
     */
    boolean existCode(Long id, String code) ;
}
