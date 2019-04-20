package com.diboot.core.binding.manager;

/**
 * 实体类与表名之间的关联关系
 * @author Mazhicheng<br>
 * @version 1.0<br>
 * @date 2019/04/03 <br>
 */

import com.baomidou.mybatisplus.annotation.TableName;
import com.diboot.core.util.S;
import com.diboot.core.util.V;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实体类与表名之间的关联关系
 * @author Mazhicheng<br>
 * @version 1.0<br>
 * @date 2019/04/03 <br>
 */
public class EntityToTableCacheManager {
    /**
     * entity类-table表名的映射关系
     */
    private static Map<String, String> CLASSNAME_TABLENAME_MAP = new ConcurrentHashMap<>();

    /**
     * 获取表名
     * @param entityClass
     * @return
     */
    public static String getTableName(Class entityClass){
        String tableName = CLASSNAME_TABLENAME_MAP.get(entityClass.getName());
        if(V.isEmpty(tableName)){
            // 获取当前VO的注解
            TableName annotation = (TableName) entityClass.getAnnotation(TableName.class);
            if(annotation != null){
                tableName = annotation.value();
            }
            else{
                tableName = S.toSnakeCase(entityClass.getSimpleName());
            }
            CLASSNAME_TABLENAME_MAP.put(entityClass.getName(), tableName);
        }
        return tableName;
    }
}