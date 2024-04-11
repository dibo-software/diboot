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
package com.diboot.iam.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.diboot.core.mapper.BaseCrudMapper;
import com.diboot.iam.entity.IamRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色Mapper
 *
 * @author mazc@dibo.ltd
 * @version 2.0
 * @date 2019-12-03
 */
@Mapper
public interface IamRoleMapper extends BaseCrudMapper<IamRole> {

    /**
     * 根据code查询角色 查询租户id = 0
     *
     * @param code
     * @param deleted
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select("SELECT * FROM dbt_iam_role WHERE is_deleted = #{deleted} AND tenant_id = '0' AND code = #{code}")
    List<IamRole> findByCode(@Param("code") String code, @Param("deleted") Object deleted);

    /**
     * 根据角色id列表查询角色
     *
     * @param ids
     * @param deleted
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select({"<script>",
            "SELECT * FROM dbt_iam_role WHERE is_deleted = #{deleted} ",
            "AND id in <foreach collection = \"ids\" item = \"id\" open = '(' close = ')' separator = ','>#{id}</foreach>",
            "</script>"})
    List<IamRole> findByIds(@Param("ids") List<String> ids, @Param("deleted") Object deleted);
}

