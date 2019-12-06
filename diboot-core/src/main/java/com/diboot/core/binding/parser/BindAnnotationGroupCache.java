package com.diboot.core.binding.parser;

import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  VO对象中的绑定注解 缓存管理类
 * @author Mazhicheng<br>
 * @version 1.0<br>
 * @date 2019/04/03 <br>
 */
public class BindAnnotationGroupCache {
    /**
     * VO类-绑定注解缓存
     */
    private static Map<Class, BindAnnotationGroup> allVoBindAnnotationCacheMap = new ConcurrentHashMap<>();

    /**
     * 获取指定class对应的Bind相关注解
     * @param voClass
     * @return
     */
    public static BindAnnotationGroup getBindAnnotationGroup(Class voClass){
        BindAnnotationGroup group = allVoBindAnnotationCacheMap.get(voClass);
        if(group == null){
            // 获取注解并缓存
            group = new BindAnnotationGroup();
            // 获取当前VO的注解
            List<Field> fields = BeanUtils.extractAllFields(voClass);
            if(fields != null){
                for (Field field : fields) {
                    //遍历属性
                    Annotation[] annotations = field.getDeclaredAnnotations();
                    if (V.isEmpty(annotations)) {
                        continue;
                    }
                    for (Annotation annotation : annotations) {
                        Class<?> setterObjClazz = field.getType();
                        if(setterObjClazz.equals(java.util.List.class) || setterObjClazz.equals(java.util.Collections.class)){
                            // 如果是集合，获取其泛型参数class
                            Type genericType = field.getGenericType();
                            if(genericType instanceof ParameterizedType){
                                ParameterizedType pt = (ParameterizedType) genericType;
                                setterObjClazz = (Class<?>)pt.getActualTypeArguments()[0];
                            }
                        }
                        group.addBindAnnotation(field.getName(), setterObjClazz, annotation);
                    }
                }
            }
            allVoBindAnnotationCacheMap.put(voClass, group);
        }
        // 返回归类后的注解对象
        return group;
    }

}
