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
package com.diboot.core.binding.binder.remote;

import com.diboot.core.exception.InvalidUsageException;
import com.diboot.core.util.ContextHolder;
import com.diboot.core.util.JSON;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 远程绑定manager
 * @author JerryMa
 * @version v2.4.0
 * @date 2021/11/1
 * Copyright © diboot.com
 */
@Slf4j
public class RemoteBindingManager {

    /**
     * RemoteBindingProvider 实例缓存
     */
    private static Map<String, RemoteBindingProvider> MODULE_PROVIDER_MAP;

    /**
     * 从远程接口抓取 Entity List
     * @param module
     * @param remoteBindDTO
     * @param entityClass
     * @param <T>
     * @return
     */
    public static <T> List<T> fetchEntityList(String module, RemoteBindDTO remoteBindDTO, Class<T> entityClass) {
        remoteBindDTO.setResultType("Entity");
        RemoteBindingProvider bindingProvider = getRemoteBindingProvider(module);
        JsonResult<String> jsonResult = bindingProvider.loadBindingData(remoteBindDTO);
        if(V.equals(jsonResult.getCode(), 0)){
            log.debug("获取到绑定数据: {}", jsonResult.getData());
            List<T> entityList = JSON.parseArray(jsonResult.getData(), entityClass);
            return entityList;
        }
        else{
            log.warn("获取绑定数据失败: {}", jsonResult.getMsg());
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * 从远程接口抓取 Map List
     * @param module
     * @param remoteBindDTO
     * @return
     */
    public static List<Map<String, Object>> fetchMapList(String module, RemoteBindDTO remoteBindDTO) {
        remoteBindDTO.setResultType("Entity");
        RemoteBindingProvider bindingProvider = getRemoteBindingProvider(module);
        JsonResult<String> jsonResult = bindingProvider.loadBindingData(remoteBindDTO);
        if(V.equals(jsonResult.getCode(), 0)){
            log.debug("获取到绑定数据: {}", jsonResult.getData());
            return JSON.parseArray(jsonResult.getData(), new TypeReference<List<Map<String, Object>>>(){});
        }
        else{
            log.warn("获取绑定数据失败: {}", jsonResult.getMsg());
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * 获取实例
     * @return
     */
    private synchronized static RemoteBindingProvider getRemoteBindingProvider(String module){
        if(MODULE_PROVIDER_MAP == null){
            MODULE_PROVIDER_MAP = new ConcurrentHashMap<>();
        }
        return MODULE_PROVIDER_MAP.computeIfAbsent(module, key -> {
            RemoteBindingProviderFactory factory = ContextHolder.getBean(RemoteBindingProviderFactory.class);
            if(factory == null) {
                throw new InvalidUsageException("RemoteBindingProviderFactory 未实现，无法使用远程绑定功能！");
            }
            return factory.create(module);
        });
    }

}
