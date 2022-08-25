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
package com.diboot.core.binding.helper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.diboot.core.binding.cache.BindingCacheManager;
import com.diboot.core.binding.parser.EntityInfoCache;
import com.diboot.core.config.Cons;
import com.diboot.core.service.BaseService;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.vo.Pagination;

import java.util.List;

/**
 * Service适配器
 * @author mazc@dibo.ltd
 * @version v2.2.0
 * @date 2020/12/21
 */
public class ServiceAdaptor {

    /**
     * 查询一个实体
     * @param iService
     * @param queryWrapper
     * @param <E>
     * @return
     */
    public static <E> E getSingleEntity(IService<E> iService, QueryWrapper queryWrapper){
        if(iService instanceof BaseService){
            BaseService baseService = (BaseService)iService;
            return (E)baseService.getSingleEntity(queryWrapper);
        }
        else{
            return (E)iService.getOne(queryWrapper);
        }
    }

    /**
     * 查询实体列表
     * @param iService
     * @param queryWrapper
     * @param <E>
     * @return
     */
    public static <E> List<E> queryList(IService<E> iService, QueryWrapper<E> queryWrapper){
        if(iService instanceof BaseService){
            BaseService baseService = (BaseService)iService;
            return (List<E>)baseService.getEntityList(queryWrapper);
        }
        else{
            return iService.list(queryWrapper);
        }
    }

    /**
     * 查询一页的实体列表
     * @param iService
     * @param queryWrapper
     * @param pagination
     * @param <E>
     * @return
     */
    public static <E> List<E> queryList(IService iService, QueryWrapper<E> queryWrapper, Pagination pagination, Class entityClass) {
        if(iService instanceof BaseService){
            BaseService baseService = (BaseService)iService;
            return (List<E>)baseService.getEntityList(queryWrapper, pagination);
        }
        else{
            if(queryWrapper.getEntityClass() == null) {
                queryWrapper.setEntityClass(entityClass);
            }
            if(pagination != null){
                IPage<E> page = convertToIPage(pagination, entityClass);
                page = iService.page(page, queryWrapper);
                // 如果重新执行了count进行查询，则更新pagination中的总数
                if(page.searchCount()){
                    pagination.setTotalCount(page.getTotal());
                }
                return page.getRecords();
            }
            else{
                return iService.list(queryWrapper);
            }
        }
    }


    /**
     * 转换为IPage
     * @param pagination 分页
     * @return
     */
    public static <E> Page<E> convertToIPage(Pagination pagination, Class<?> entityClass) {
        if(pagination == null){
            return null;
        }
        // 如果是默认id排序
        if(pagination.isDefaultOrderBy()){
            // 优化排序
            String pk = ContextHelper.getIdFieldName(entityClass);
            // 主键非有序id字段，需要清空默认排序以免报错
            if(!Cons.FieldName.id.name().equals(pk)){
                pagination.clearDefaultOrder();
                //设置时间排序
                EntityInfoCache entityInfoCache = BindingCacheManager.getEntityInfoByClass(entityClass);
                if(entityInfoCache.containsColumn(Cons.COLUMN_CREATE_TIME)){
                    pagination.setDefaultCreateTimeOrderBy();
                }
            }
        }
        return pagination.setEntityClass(entityClass).toPage();
    }

}
