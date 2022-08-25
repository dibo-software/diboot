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
package com.diboot.core.binding.query.dynamic;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.diboot.core.binding.helper.ServiceAdaptor;
import com.diboot.core.binding.parser.ParserCache;
import com.diboot.core.exception.InvalidUsageException;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.vo.Pagination;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 动态查询wrapper
 * @author Mazc@dibo.ltd
 * @version v2.0
 * @date 2020/04/16
 */
public class ExtQueryWrapper<T> extends QueryWrapper<T> {
    /**
     * 主实体class
     */
    @Getter @Setter
    private Class<T> mainEntityClass;

    /**
     * 获取entity表名
     * @return
     */
    public String getEntityTable(){
        if(this.mainEntityClass == null) {
            this.mainEntityClass = getEntityClass();
        }
        return ParserCache.getEntityTableName(this.mainEntityClass);
    }

    /**
     * 查询一条数据
     * @param entityClazz
     * @return
     */
    public T queryOne(Class<T> entityClazz){
        this.mainEntityClass = entityClazz;
        IService<T> iService = ContextHelper.getIServiceByEntity(this.mainEntityClass);
        if(iService != null){
            return ServiceAdaptor.getSingleEntity(iService, this);
        }
        else{
            throw new InvalidUsageException("查询对象无BaseService/IService实现: "+this.mainEntityClass.getSimpleName());
        }
    }

    /**
     * 查询一条数据
     * @param entityClazz
     * @return
     */
    public List<T> queryList(Class<T> entityClazz){
        this.mainEntityClass = entityClazz;
        IService iService = ContextHelper.getIServiceByEntity(entityClazz);
        if(iService != null){
            return ServiceAdaptor.queryList(iService, this);
        }
        else{
            throw new InvalidUsageException("查询对象无BaseService/IService实现: "+entityClazz.getSimpleName());
        }
    }

    /**
     * 查询一条数据
     * @param entityClazz
     * @return
     */
    public List queryList(Class<T> entityClazz, Pagination pagination){
        this.mainEntityClass = entityClazz;
        IService iService = ContextHelper.getIServiceByEntity(entityClazz);
        if(iService != null){
            return ServiceAdaptor.queryList(iService, this, pagination, entityClazz);
        }
        else{
            throw new InvalidUsageException("查询对象无BaseService/IService实现: "+entityClazz.getSimpleName());
        }
    }

}
