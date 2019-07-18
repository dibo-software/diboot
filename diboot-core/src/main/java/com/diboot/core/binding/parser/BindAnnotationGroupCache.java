package com.diboot.core.binding.parser;

import com.diboot.core.binding.parser.BindAnnotationGroup;
import com.diboot.core.util.V;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
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
            Field[] fields = voClass.getDeclaredFields();
            if(fields != null){
                for (Field field : fields) {
                    //遍历属性
                    Annotation[] annotations = field.getDeclaredAnnotations();
                    if (V.isEmpty(annotations)) {
                        continue;
                    }
                    for (Annotation annotation : annotations) {
                        group.addBindAnnotation(field.getName(), annotation);
                    }
                }
            }
            allVoBindAnnotationCacheMap.put(voClass, group);
        }
        // 返回归类后的注解对象
        return group;
    }

}
