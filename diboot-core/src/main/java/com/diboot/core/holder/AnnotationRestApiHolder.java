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
package com.diboot.core.holder;

import com.diboot.core.cache.StaticMemoryCacheManager;
import com.diboot.core.config.Cons;
import com.diboot.core.holder.api.CollectThisApi;
import com.diboot.core.holder.api.RestApi;
import com.diboot.core.holder.api.RestApiWrapper;
import com.diboot.core.util.AnnotationUtils;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.V;
import com.diboot.core.vo.ApiUri;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 注解 RestApi 信息缓存
 * @author JerryMa
 * @version v2.2.1
 * @date 2021/4/23
 * Copyright © diboot.com
 */
@Slf4j
public class AnnotationRestApiHolder {

    /**
     * 实体相关定义缓存管理器
     */
    private static StaticMemoryCacheManager cacheManager;
    /**
     * 类-ApiWrapper缓存key
     */
    private static final String CACHE_NAME_CLASS_TO_WRAPPER = "CLASS_TO_WRAPPER";
    /**
     * 类别-Api接口集合 缓存key
     */
    private static final String CACHE_NAME_CATEGORY_TO_APILIST = "CATEGORY_TO_API_LIST";

    private synchronized static StaticMemoryCacheManager getCacheManager(){
        if(cacheManager == null){
            cacheManager = new StaticMemoryCacheManager(
                    CACHE_NAME_CLASS_TO_WRAPPER,
                    CACHE_NAME_CATEGORY_TO_APILIST);
        }
        return cacheManager;
    }

    /**
     * 获取默认分类的 RestApi 接口
     * @return
     */
    public static List<RestApi> getDefaultCategoryRestApiList(){
        return getRestApiList("default");
    }

    /**
     * 获取 RestApi 接口
     * @param category
     * @return
     */
    public synchronized static List<RestApi> getRestApiList(String category){
        List<RestApi> apiList = getCacheManager()
                .getCacheObj(CACHE_NAME_CATEGORY_TO_APILIST, category, List.class);
        if(apiList == null && getCacheManager().isUninitializedCache(CACHE_NAME_CATEGORY_TO_APILIST)){
            initRestApiCache();
            apiList = getCacheManager()
                    .getCacheObj(CACHE_NAME_CATEGORY_TO_APILIST, category, List.class);
        }
        return apiList;
    }

    /**
     * 获取 RestApiWrapper 接口
     * @param className
     * @return
     */
    public synchronized static RestApiWrapper getRestApiWrapper(String className){
        RestApiWrapper apiWrapper = getCacheManager()
                .getCacheObj(CACHE_NAME_CLASS_TO_WRAPPER, className, RestApiWrapper.class);
        if(apiWrapper == null && getCacheManager().isUninitializedCache(CACHE_NAME_CLASS_TO_WRAPPER)){
            initRestApiCache();
            apiWrapper = getCacheManager()
                        .getCacheObj(CACHE_NAME_CLASS_TO_WRAPPER, className, RestApiWrapper.class);
        }
        return apiWrapper;
    }

    /**
     * 初始化
     */
    private synchronized static void initRestApiCache() {
        List<Object> controllerList = ContextHelper.getBeansByAnnotation(RestController.class);
        if(V.isEmpty(controllerList)) {
            return;
        }
        for (Object obj : controllerList) {
            Class controllerClass = BeanUtils.getTargetClass(obj);
            // 注解
            CollectThisApi anno = AnnotationUtils.findAnnotation(controllerClass, CollectThisApi.class);
            String title = anno != null? anno.name() : controllerClass.getSimpleName();
            RestApiWrapper wrapper = new RestApiWrapper(controllerClass.getName(), title, anno);
            buildRestApisInClass(wrapper, controllerClass);
            // 当类注解存在 或 接口方法注解存在，则缓存
            if(anno != null || V.notEmpty(wrapper.getChildren())){
                getCacheManager()
                        .putCacheObj(CACHE_NAME_CLASS_TO_WRAPPER, wrapper.getClassName(), wrapper);
                if(V.notEmpty(wrapper.getChildren())){
                    Map<String, List<RestApi>> categoryApisMap = new HashMap<>(8);
                    for(RestApi api : wrapper.getChildren()){
                        List<RestApi> categoryApis = categoryApisMap.get(api.getCategory());
                        if(categoryApis == null){
                            categoryApis = new ArrayList<>();
                            categoryApisMap.put(api.getCategory(), categoryApis);
                        }
                        categoryApis.add(api);
                    }
                    for(Map.Entry<String, List<RestApi>> entry : categoryApisMap.entrySet()){
                        List<RestApi> categoryApis = getCacheManager()
                                .getCacheObj(CACHE_NAME_CATEGORY_TO_APILIST, entry.getKey(), List.class);
                        if(categoryApis == null){
                            categoryApis = entry.getValue();
                        }
                        else{
                            for(RestApi api : entry.getValue()){
                                if(!categoryApis.contains(api)){
                                    categoryApis.add(api);
                                }
                            }
                        }
                        getCacheManager()
                                .putCacheObj(CACHE_NAME_CATEGORY_TO_APILIST, entry.getKey(), categoryApis);
                    }
                }
            }
        }
    }

    /**
     * 构建controller中的Api权限
     * @param wrapper
     * @param controllerClass
     */
    private static void buildRestApisInClass(RestApiWrapper wrapper, Class controllerClass) {
        String urlPrefix = "";
        RequestMapping requestMapping = AnnotationUtils.findAnnotation(controllerClass, RequestMapping.class);
        if(requestMapping != null){
            urlPrefix = AnnotationUtils.getNotEmptyStr(requestMapping.value(), requestMapping.path());
        }
        List<Method> annoMethods = AnnotationUtils.extractAnnotationMethods(controllerClass, CollectThisApi.class);
        if(V.notEmpty(annoMethods)){
            for(Method method : annoMethods){
                // 忽略私有方法
                if(Modifier.isPrivate(method.getModifiers())){
                    continue;
                }
                // 处理Annotation注解
                CollectThisApi restApiAnno = AnnotationUtils.getAnnotation(method, CollectThisApi.class);
                // 提取方法上的注解url
                ApiUri apiUri = AnnotationUtils.extractRequestMethodAndMappingUrl(method);
                if(apiUri.isEmpty()){
                    continue;
                }
                // 提取请求url-注解的关系
                buildRestApi(wrapper, urlPrefix, apiUri, restApiAnno);
            }
        }
    }

    /**
     * 构建restApiList
     * @param wrapper
     * @param urlPrefix
     * @param apiUri
     */
    private static void buildRestApi(RestApiWrapper wrapper, String urlPrefix, ApiUri apiUri, CollectThisApi annotation){
        String requestMethod = apiUri.getMethod(), url = apiUri.getUri();
        for(String m : requestMethod.split(Cons.SEPARATOR_COMMA)){
            for(String u : url.split(Cons.SEPARATOR_COMMA)){
                if(V.notEmpty(urlPrefix)){
                    for(String path : urlPrefix.split(Cons.SEPARATOR_COMMA)){
                        RestApi restApi = new RestApi().setApiMethod(m).setApiUri(path + u)
                                .setCategory(annotation.category()).setApiName(annotation.name());
                        wrapper.addChild(restApi);
                    }
                }
                else{
                    RestApi restApi = new RestApi().setApiMethod(m).setApiUri(u)
                            .setCategory(annotation.category()).setApiName(annotation.name());
                    wrapper.addChild(restApi);
                }
            }
        }
    }

}
