/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.core.util;

import com.diboot.core.vo.ApiUri;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 注解相关工具类
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/23
 */
@Slf4j
public class AnnotationUtils extends org.springframework.core.annotation.AnnotationUtils {

    /**
     * 从Method中提取请求方法和URL
     * @param method
     * @return
     */
    public static ApiUri extractRequestMethodAndMappingUrl(Method method){
        String requestMethod = null, url = null;
        if(method.getAnnotation(GetMapping.class) != null){
            GetMapping anno = AnnotationUtils.getAnnotation(method, GetMapping.class);
            requestMethod = RequestMethod.GET.name();
            url = getNotEmptyStr(anno.value(), anno.path());
        }
        else if(method.getAnnotation(PostMapping.class) != null){
            PostMapping anno = AnnotationUtils.getAnnotation(method, PostMapping.class);
            requestMethod = RequestMethod.POST.name();
            url = getNotEmptyStr(anno.value(), anno.path());
        }
        else if(method.getAnnotation(PutMapping.class) != null){
            PutMapping anno = AnnotationUtils.getAnnotation(method, PutMapping.class);
            requestMethod = RequestMethod.PUT.name();
            url = getNotEmptyStr(anno.value(), anno.path());
        }
        else if(method.getAnnotation(DeleteMapping.class) != null){
            DeleteMapping anno = AnnotationUtils.getAnnotation(method, DeleteMapping.class);
            requestMethod = RequestMethod.DELETE.name();
            url = getNotEmptyStr(anno.value(), anno.path());
        }
        else if(method.getAnnotation(PatchMapping.class) != null){
            PatchMapping anno = AnnotationUtils.getAnnotation(method, PatchMapping.class);
            requestMethod = RequestMethod.PATCH.name();
            url = getNotEmptyStr(anno.value(), anno.path());
        }
        else if(AnnotationUtils.getAnnotation(method, RequestMapping.class) != null){
            RequestMapping anno = AnnotationUtils.getAnnotation(method, RequestMapping.class);
            if(V.notEmpty(anno.method())){
                List<String> methods = Arrays.stream(anno.method()).map(m -> m.name()).collect(Collectors.toList());
                requestMethod = S.join(methods);
            }
            else{
                requestMethod = "GET,POST";
            }
            url = getNotEmptyStr(anno.value(), anno.path());
        }
        else{
            log.warn("无法识别到URL Mapping相关注解: "+method.getName());
        }
        return new ApiUri(requestMethod, url);
    }

    /**
     * 获取非空字符串
     * @param values
     * @param paths
     * @return
     */
    public static String getNotEmptyStr(String[] values, String[] paths){
        if(V.notEmpty(values) && values[0] != null){
            return S.join(values);
        }
        else if(V.notEmpty(paths) && paths[0] != null){
            return S.join(paths);
        }
        return "";
    }

    /**
     * 获取类中所有包含注解的方法（包含父类中）
     * @param clazz
     * @return
     */
    public static List<Method> extractAnnotationMethods(Class<?> clazz, Class<? extends Annotation> annotationClass){
        List<Method> methodList = new ArrayList<>();
        while (clazz != null) {
            Method[] methods = clazz.getDeclaredMethods();
            if(V.notEmpty(methods)){
                //被重写属性，以子类override的为准
                Arrays.stream(methods).forEach((method)->{
                    if(AnnotationUtils.getAnnotation(method, annotationClass) != null){
                        methodList.add(method);
                    }
                });
            }
            clazz = clazz.getSuperclass();
        }
        return methodList;
    }
}
