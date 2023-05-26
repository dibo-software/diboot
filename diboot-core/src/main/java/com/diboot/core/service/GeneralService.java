package com.diboot.core.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.diboot.core.dto.SortParamDTO;
import com.diboot.core.entity.BaseEntity;
import com.diboot.core.util.IGetter;
import com.diboot.core.util.ISetter;
import com.diboot.core.vo.LabelValue;
import com.diboot.core.vo.Pagination;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 通用服务接口Service
 * @author mazc@dibo.ltd
 * @version 3.0
 * @date 2023/05/25
 */
public interface GeneralService<T> {

    /**
     * 创建Entity实体
     * @param entity
     * @return true:成功, false:失败
     */
    boolean createEntity(T entity);

    /***
     * 批量创建Entity
     * @param entityList 实体对象列表
     * @return true:成功, false: 失败
     */
    boolean createEntities(Collection<T> entityList);

    /**
     * 更新Entity实体
     * @param entity
     * @return
     */
    boolean updateEntity(T entity);

    /**
     * 批量更新entity
     * @param entityList
     * @return
     */
    boolean updateEntities(Collection<T> entityList);

    /***
     * 创建或更新entity（entity.id存在则新建，否则更新）
     * @param entity
     * @return
     */
    boolean createOrUpdateEntity(T entity);

    /**
     * 批量创建或更新entity（entity.id存在则新建，否则更新）
     * @param entityList
     * @return
     */
    boolean createOrUpdateEntities(Collection<T> entityList);

    /**
     * 根据主键删除实体
     * @param id 主键
     * @return true:成功, false:失败
     */
    boolean deleteEntity(Class<T> entityClass, Serializable id);

    /**
     * 批量删除指定id的实体
     * @param entityIds
     * @return
     * @throws Exception
     */
    boolean deleteEntities(Class<T> entityClass, Collection<? extends Serializable> entityIds);

}