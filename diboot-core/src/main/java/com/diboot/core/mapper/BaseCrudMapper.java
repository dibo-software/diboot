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
package com.diboot.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.io.Serializable;

/**
 * 基础CRUD的父类Mapper
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2018/12/22
 */
public interface BaseCrudMapper<T> extends BaseMapper<T> {

    /***
     * 通过id撤回当前记录的删除状态
     * @param tableName
     * @param id
     * @return
     */
    @Update("UPDATE `${tableName}` SET is_deleted=0 WHERE id=#{id}")
    int cancelDeletedById(@Param("tableName") String tableName, @Param("id") Serializable id);
}