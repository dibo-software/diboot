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
package com.diboot.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.entity.I18nConfig;
import com.diboot.core.mapper.I18nConfigMapper;
import com.diboot.core.service.I18nConfigService;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.I18nConfigVO;
import com.diboot.core.vo.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 国际化配置 Service实现
 *
 * @author wind
 * @version v3.0.0
 * @date 2022-10-12
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "diboot.core", name = "i18n", havingValue = "true")
public class I18nConfigServiceImpl extends BaseServiceImpl<I18nConfigMapper, I18nConfig> implements I18nConfigService {

    @Override
    public Collection<List<I18nConfigVO>> getI18nList(I18nConfig entity, Pagination pagination) {
        LambdaQueryWrapper<I18nConfig> queryWrapper = Wrappers.<I18nConfig>lambdaQuery().select(I18nConfig::getCode);
        queryWrapper.eq(V.notEmpty(entity.getType()), I18nConfig::getType, entity.getType());
        queryWrapper.like(V.notEmpty(entity.getCode()), I18nConfig::getCode, entity.getCode());
        queryWrapper.like(V.notEmpty(entity.getContent()), I18nConfig::getContent, entity.getContent());
        queryWrapper.groupBy(I18nConfig::getCode);
        List<String> codes = getEntityList(queryWrapper, pagination).stream().map(I18nConfig::getCode).collect(Collectors.toList());
        if (V.isEmpty(codes)) {
            return Collections.emptyList();
        }
        QueryWrapper<I18nConfig> query = Wrappers.query();
        for (OrderItem order : pagination.toPage(entityClass).orders()) {
            query.orderBy(true, order.isAsc(), order.getColumn());
        }
        List<I18nConfigVO> entityList = getViewObjectList(query.lambda().in(I18nConfig::getCode, codes), null, I18nConfigVO.class);
        return entityList.stream().collect(Collectors.groupingBy(I18nConfig::getCode, LinkedHashMap::new,Collectors.toList())).values();
    }

    @Override
    public void bindI18nContent(List<?> voList, String getI18nCodeField, String setI18nContentField) {
        if (V.isEmpty(voList)) {
            return;
        }
        Set<String> codes = new HashSet<>();
        for (Object item : voList) {
            Object i18nCode = BeanUtils.getProperty(item, getI18nCodeField);
            if (V.notEmpty(i18nCode)) {
                codes.add(S.valueOf(i18nCode));
            }
        }
        if(V.isEmpty(codes)){
            return;
        }
        Locale locale = LocaleContextHolder.getLocale();
        LambdaQueryWrapper<I18nConfig> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.select(I18nConfig::getLanguage,I18nConfig::getCode,I18nConfig::getContent);
        queryWrapper.in(I18nConfig::getCode,codes);
        queryWrapper.in(I18nConfig::getLanguage, locale.toString(), locale.getLanguage(), Locale.getDefault().toString());
        Map<String, List<I18nConfig>> map = getEntityList(queryWrapper).stream().collect(Collectors.groupingBy(I18nConfig::getLanguage));
        List<I18nConfig> list = map.get(locale.toString());
        if (list == null && (list = map.get(locale.toString())) == null && (list = map.get(Locale.getDefault().toString())) == null) {
            return;
        }
        Map<String, String> i18nMap = list.stream().collect(Collectors.toMap(I18nConfig::getCode, I18nConfig::getContent));
        for (Object item : voList) {
            Object i18nCode = BeanUtils.getProperty(item, getI18nCodeField);
            if (V.notEmpty(i18nCode)) {
                String content = i18nMap.get(S.valueOf(i18nCode));
                if (V.notEmpty(content)) {
                    BeanUtils.setProperty(item, setI18nContentField, content);
                }
            }
        }
    }
}
