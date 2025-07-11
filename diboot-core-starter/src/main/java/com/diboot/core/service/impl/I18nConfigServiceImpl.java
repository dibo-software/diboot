/*
 * Copyright (c) 2015-2099, www.dibo.ltd (service@dibo.ltd).
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
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.cache.I18nCacheManager;
import com.diboot.core.entity.I18nConfig;
import com.diboot.core.mapper.I18nConfigMapper;
import com.diboot.core.service.I18nConfigService;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.I18nConfigVO;
import com.diboot.core.vo.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@ConditionalOnProperty(prefix = "diboot", name = "i18n", havingValue = "true")
public class I18nConfigServiceImpl extends BaseServiceImpl<I18nConfigMapper, I18nConfig> implements I18nConfigService {

    @Autowired
    private I18nCacheManager i18nCacheManager;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createOrUpdateEntities(Collection entityList) {
        if(V.isEmpty(entityList)){
            return false;
        }
        // 获取当前语言的缓存数据
        for(Object entity : entityList){
            I18nConfig i18nConfig = (I18nConfig)entity;
            Map<String, String> languageCached = i18nCacheManager.getLanguageCached(i18nConfig.getLanguage());
            if(V.notEmpty(languageCached)){
                languageCached.remove(i18nConfig.getCode());
                log.debug("I18N {}:{} 的缓存已被移除", i18nConfig.getLanguage(), i18nConfig.getCode());
            }
        }
        // 批量插入
        return super.createOrUpdateEntities(entityList);
    }

    /**
     * 数据变动前先清空缓存
     * @param entity
     */
    protected void beforeCreate(I18nConfig entity) {
        // 获取当前语言的缓存数据
        Map<String, String> languageCached = i18nCacheManager.getLanguageCached(entity.getLanguage());
        if(V.notEmpty(languageCached)){
            languageCached.remove(entity.getCode());
            log.debug("I18N {}:{} 的缓存已被移除", entity.getLanguage(), entity.getCode());
        }
    }

    /**
     * 数据变动前先清空缓存
     * @param entity
     */
    protected void beforeUpdate(I18nConfig entity) {
        beforeCreate(entity);
    }

    /**
     * 数据变动前先清空缓存
     * @param fieldKey
     * @param fieldVal
     */
    protected void beforeDelete(String fieldKey, Object fieldVal) {
        QueryWrapper<I18nConfig> queryWrapper = buildQueryWrapperByFieldValue(fieldKey, fieldVal);
        queryWrapper.lambda().select(I18nConfig::getLanguage, I18nConfig::getCode);
        List<I18nConfig> i18nConfigList = getEntityList(queryWrapper);
        if(V.isEmpty(i18nConfigList)){
            return;
        }
        i18nConfigList.forEach(this::beforeCreate);
    }

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

        for (OrderItem order : pagination.toPage(I18nConfig.class).orders()) {
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
        Locale locale = LocaleContextHolder.getLocale();
        String language = locale.toString();
        // 获取当前语言的缓存数据
        Map<String, String> languageCached = i18nCacheManager.getLanguageCached(language);
        Set<String> codes = new HashSet<>();
        for (Object item : voList) {
            Object i18nCode = BeanUtils.getProperty(item, getI18nCodeField);
            // 缓存中已经存在的数据直接赋值，不存在的进行数据库查询
            if (V.notEmpty(i18nCode)) {
                if (V.notEmpty(languageCached) && V.notEmpty(languageCached.get(i18nCode))) {
                    BeanUtils.setProperty(item, setI18nContentField, languageCached.get(i18nCode));
                    log.trace("语言环境 {} 从缓存中获取 {} 的选项数据", language, i18nCode);
                } else {
                    codes.add(S.valueOf(i18nCode));
                }
            }
        }
        if(V.isEmpty(codes)){
            return;
        }
        LambdaQueryWrapper<I18nConfig> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.select(I18nConfig::getLanguage,I18nConfig::getCode,I18nConfig::getContent);
        queryWrapper.in(I18nConfig::getCode, codes);
        queryWrapper.eq(I18nConfig::getLanguage, language);
        List<I18nConfig> i18nConfigList = getEntityList(queryWrapper);
        if(V.isEmpty(i18nConfigList)){
            log.warn("未获取到国际化翻译 {}: {}，请检查国际化翻译配置", language, codes);
            return;
        }
        Map<String, String> i18nMap = i18nConfigList.stream().collect(Collectors.toMap(I18nConfig::getCode, I18nConfig::getContent));
        // 将查询的数据缓存
        i18nCacheManager.cacheLanguage(language, i18nMap);
        // 将剩下的国际化数据进行赋值
        for (Object item : voList) {
            Object i18nCode = BeanUtils.getProperty(item, getI18nCodeField);
            if (V.notEmpty(i18nCode)) {
                if (!codes.contains(i18nCode)) {
                    continue;
                }
                String content = i18nMap.get(S.valueOf(i18nCode));
                if (V.notEmpty(content)) {
                    BeanUtils.setProperty(item, setI18nContentField, content);
                }
            }
        }

    }

    @Override
    public Map<String, String> translate(List<String> i18nKeys) {
        if(V.isEmpty(i18nKeys)){
            return Collections.emptyMap();
        }
        Map<String, String> i18nTranslateMap = new HashMap<>(i18nKeys.size());
        Locale locale = LocaleContextHolder.getLocale();
        String language = locale.toString();
        // 获取当前语言的缓存数据
        Map<String, String> languageCached = i18nCacheManager.getLanguageCached(language);
        Set<String> noCachedCodes = new HashSet<>();
        for (String i18nCode : i18nKeys) {
            if (V.notEmpty(languageCached) && V.notEmpty(languageCached.get(i18nCode))) {
                i18nTranslateMap.put(i18nCode, languageCached.get(i18nCode));
                log.trace("从缓存中获取国际化翻译 {}: {} ", language, i18nCode);
            }
            else {
                noCachedCodes.add(i18nCode);
            }
        }
        if(V.isEmpty(noCachedCodes)){
            return i18nTranslateMap;
        }
        LambdaQueryWrapper<I18nConfig> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.select(I18nConfig::getLanguage, I18nConfig::getCode, I18nConfig::getContent);
        queryWrapper.eq(I18nConfig::getLanguage, language);
        queryWrapper.in(I18nConfig::getCode, noCachedCodes);
        List<I18nConfig> i18nConfigList = getEntityList(queryWrapper);
        if(V.isEmpty(i18nConfigList)){
            log.warn("未获取到国际化翻译 {}: {}，请检查国际化翻译配置", language, noCachedCodes);
            return i18nTranslateMap;
        }
        Map<String, String> i18nMap = i18nConfigList.stream().collect(Collectors.toMap(I18nConfig::getCode, I18nConfig::getContent));
        // 将查询的数据缓存
        i18nCacheManager.cacheLanguage(language, i18nMap);
        i18nTranslateMap.putAll(i18nMap);
        // 返回全部结果
        return i18nTranslateMap;
    }

    @Override
    public void updateI18nContent(String language, String code, String newContent) {
        LambdaUpdateWrapper<I18nConfig> updateWrapper =
                Wrappers.<I18nConfig>lambdaUpdate()
                        .set(I18nConfig::getContent, newContent)
                        .eq(I18nConfig::getLanguage, language)
                        .eq(I18nConfig::getCode, code);
        updateEntity(updateWrapper);
        Map<String, String> i18nItemCache = new HashMap<>();
        i18nItemCache.put(code, newContent);
        i18nCacheManager.cacheLanguage(language, i18nItemCache);
        log.debug("I18N {}:{} 的缓存已被更新为: {}", language, code, newContent);
    }

}
