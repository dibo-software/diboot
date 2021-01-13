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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.diboot.core.binding.query.dynamic.DynamicSqlProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Map;

/**
 * 通用联表查询Mapper
 * @author mazc@dibo.ltd
 * @version 2018/12/22
 */
@Mapper
public interface DynamicQueryMapper {

    /**
     * 动态SQL查询
     * @return
     */
    @SelectProvider(type= DynamicSqlProvider.class, method="buildSqlForList")
    List<Map<String, Object>> queryForList(@Param(Constants.WRAPPER) QueryWrapper ew);

    /**
     * 动态SQL查询
     * @param page
     * @return
     */
    @SelectProvider(type= DynamicSqlProvider.class, method="buildSqlForListWithPage")
    IPage<Map<String, Object>> queryForListWithPage(Page<?> page, @Param(Constants.WRAPPER) QueryWrapper ew);

}