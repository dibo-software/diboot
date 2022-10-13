/*
 * Copyright (c) 2015-2022, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.core.service;

import com.diboot.core.entity.I18nConfig;
import com.diboot.core.vo.I18nConfigVO;
import com.diboot.core.vo.Pagination;

import java.util.Collection;
import java.util.List;

/**
 * 国际化配置 Service接口
 *
 * @author wind
 * @version v3.0.0
 * @date 2022-10-12
 */
public interface I18nConfigService extends BaseService<I18nConfig> {

    /**
     * 获取国际化配置列表
     *
     * @param entity     查询参数
     * @param pagination 分页
     * @return 国际化配置列表
     */
    Collection<List<I18nConfigVO>> getI18nList(I18nConfig entity, Pagination pagination);

    /**
     * 绑定国际化翻译内容
     *
     * @param voList              VO列表
     * @param getI18nCodeField    Get资源标识属性
     * @param setI18nContentField Set翻译内容属性
     */
    void bindI18nContent(List<?> voList, String getI18nCodeField, String setI18nContentField);

}