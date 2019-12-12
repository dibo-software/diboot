package com.diboot.core.entity;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

/**
 * Entity的List包装类，用于接收List并绑定校验的情况
 * @author Mazhicheng
 * @version 2.0
 * @date 2018/11/8
 */
public class EntityList<T extends BaseEntity> {

    @Valid
    private final List<T> entityList;

    public EntityList(final T... entities){
        this.entityList = Arrays.asList(entities);
    }

    public EntityList(final List<T> entityList){
        this.entityList = entityList;
    }

    public List<T> getEntityList(){
        return entityList;
    }

}