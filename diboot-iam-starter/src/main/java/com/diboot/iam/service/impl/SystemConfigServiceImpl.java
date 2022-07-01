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
package com.diboot.iam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.JSON;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.LabelValue;
import com.diboot.iam.config.SystemConfigInjection;
import com.diboot.iam.config.SystemConfigTest;
import com.diboot.iam.config.SystemConfigType;
import com.diboot.iam.entity.SystemConfig;
import com.diboot.iam.mapper.SystemConfigMapper;
import com.diboot.iam.service.SystemConfigService;
import com.diboot.iam.vo.SystemConfigVO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统配置相关Service实现
 *
 * @author wind
 * @version v2.5.0
 * @date 2022-01-13
 */
@Slf4j
@Service
public class SystemConfigServiceImpl extends BaseServiceImpl<SystemConfigMapper, SystemConfig> implements SystemConfigService {

    @Autowired(required = false)
    private List<SystemConfigInjection> configInjections;

    @Getter
    private final List<LabelValue> typeList = new ArrayList<>();
    @Getter
    private final Map<String, List<Enum<? extends SystemConfigType>>> configItemsMap = new HashMap<>();
    private final Map<String, SystemConfigTest<?>> configTestMap = new HashMap<>();
    private final Map<String, Class<?>> configTestDataClassMap = new HashMap<>();

    @PostConstruct
    protected void initialize() {
        if (configInjections == null) {
            return;
        }
        for (SystemConfigInjection configInjection : configInjections) {
            List<Class<? extends Enum<? extends SystemConfigType>>> types = configInjection.getTypes();
            if (V.isEmpty(types)) {
                continue;
            }
            for (Class<? extends Enum<? extends SystemConfigType>> configType : types) {
                Enum<? extends SystemConfigType>[] enumConstants = configType.getEnumConstants();
                if (enumConstants.length == 0) {
                    continue;
                }
                String type = configType.getSimpleName();
                LabelValue labelValue = new LabelValue(((SystemConfigType) enumConstants[0]).typeLabel(), type);
                if (SystemConfigTest.class.isAssignableFrom(configType)) {
                    configTestMap.put(type, (SystemConfigTest<?>) enumConstants[0]);
                    Class<?> testDataClass = findTestDataClass(ResolvableType.forClass(configType));
                    configTestDataClassMap.put(type, testDataClass);
                    labelValue.setExt(BeanUtils.extractAllFields(testDataClass).stream().map(Field::getName).collect(Collectors.toSet()));
                }
                typeList.add(labelValue);
                configItemsMap.put(type, Arrays.asList(enumConstants));
            }
        }
    }

    /**
     * 获取测试接口数据类型
     *
     * @param resolvableType
     * @return
     */
    private Class<?> findTestDataClass(ResolvableType resolvableType) {
        for (ResolvableType anInterface : resolvableType.getInterfaces()) {
            Class<?> aClass = anInterface.toClass();
            if (SystemConfigTest.class.equals(aClass)) {
                return anInterface.getGenerics()[0].toClass();
            } else if (SystemConfigTest.class.isAssignableFrom(aClass)) {
                return findTestDataClass(anInterface);
            }
        }
        return null;
    }

    @Override
    public List<SystemConfigVO> getConfigByType(String type) {
        if (!configItemsMap.containsKey(type)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<SystemConfig> queryWrapper = Wrappers.<SystemConfig>lambdaQuery().eq(SystemConfig::getType, type);
        Map<String, SystemConfigVO> configInfoMap = this.getViewObjectList(queryWrapper, null, SystemConfigVO.class)
                .stream().collect(Collectors.toMap(SystemConfig::getProp, e -> e));
        return configItemsMap.get(type).stream().map(item -> {
            SystemConfigType configProp = (SystemConfigType) item;
            Object defaultValue = configProp.buildDefaultValue();
            return configInfoMap.getOrDefault(item.name(), new SystemConfigVO() {{
                        setType(type).setProp(item.name()).setValue(S.valueOf(defaultValue));
                    }}).setPropLabel(S.defaultIfEmpty(configProp.propLabel(), item.name())).setDefaultValue(defaultValue)
                    .setOptions(configProp.options()).setRequired(configProp.required()).setOrdinal(item.ordinal());
        }).sorted(Comparator.comparingInt(SystemConfigVO::getOrdinal)).collect(Collectors.toList());
    }

    @Override
    public boolean deleteByTypeAndProp(String type, String prop) {
        LambdaQueryWrapper<SystemConfig> queryWrapper = Wrappers.<SystemConfig>lambdaQuery()
                .eq(SystemConfig::getType, type).eq(V.notEmpty(prop), SystemConfig::getProp, prop);
        return this.deleteEntities(queryWrapper);
    }

    @Override
    public void configTest(String type, Map<String, Object> data) {
        SystemConfigTest systemConfigTest = configTestMap.get(type);
        if (systemConfigTest == null) {
            throw new BusinessException("系统配置`" + type + "`未实现测试方法");
        }
        Class<?> testDataClass = configTestDataClassMap.get(type);
        systemConfigTest.test(Object.class.equals(testDataClass) ? null : JSON.parseObject(JSON.stringify(data), testDataClass));
    }

    @Override
    public String findConfigValue(String type, String prop) {
        SystemConfig info = this.getSingleEntity(Wrappers.<SystemConfig>lambdaQuery()
                .eq(SystemConfig::getType, type).eq(SystemConfig::getProp, prop));
        return info == null ? null : info.getValue();
    }

    @Override
    public Map<String, String> getConfigMapByType(String type) {
        return getEntityList(Wrappers.<SystemConfig>lambdaQuery()
                .select(SystemConfig::getProp, SystemConfig::getValue).eq(SystemConfig::getType, type))
                .stream().collect(Collectors.toMap(SystemConfig::getProp, SystemConfig::getValue));
    }

}
