package com.diboot.core.binding;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.service.BaseService;
import com.diboot.core.util.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/1/19
 */
public class EntityListBinder<T> extends EntityBinder<T> {
    private static final Logger log = LoggerFactory.getLogger(EntityListBinder.class);

    /***
     * 构造方法
     * @param serviceInstance
     * @param voList
     */
    public EntityListBinder(BaseService<T> serviceInstance, List voList){
        this.referencedService = serviceInstance;
        this.annoObjectList = voList;
        this.queryWrapper = new QueryWrapper<T>();
    }

    @Override
    protected void bindingResult(String fkName, List<T> list) {
        Map<Object, List<T>> valueEntityListMap = new HashMap<>(list.size());
        for(T entity : list){
            Object keyValue = BeanUtils.getProperty(entity, fkName);
            List<T> entityList = valueEntityListMap.get(keyValue);
            if(entityList == null){
                entityList = new ArrayList<>();
                valueEntityListMap.put(keyValue, entityList);
            }
            entityList.add(entity);
        }
        // 绑定
        BeanUtils.bindPropValueOfList(annoObjectField, annoObjectList, annoObjectForeignKey, valueEntityListMap);
    }

}
