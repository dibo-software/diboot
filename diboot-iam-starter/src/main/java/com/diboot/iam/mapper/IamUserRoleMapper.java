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
import com.diboot.iam.entity.IamUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户角色关联Mapper
 *
 * @author mazc@dibo.ltd
 * @version 2.0
 * @date 2019-12-17
 */
@Mapper
public interface IamUserRoleMapper extends BaseCrudMapper<IamUserRole> {

    /**
     * 查询角色ID列表
     *
     * @param tenantId
     * @param userType
     * @param userId
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select({"SELECT role_id FROM dbt_iam_user_role WHERE is_deleted = #{deleted} ",
            "AND tenant_id = #{tenantId} AND user_type = #{userType} AND user_id = #{userId}"})
    List<String> selectRoleIds(@Param("tenantId") String tenantId, @Param("userType") String userType,
                             @Param("userId") String userId, @Param("deleted") Object deleted);

    /**
     * 根据租户ID和角色ID获取1个用户ID
     *
     * @param tenantId 租户ID
     * @param roleId   角色ID
     * @param deleted  is_deleted
     * @return 用户ID
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select("SELECT user_id FROM dbt_iam_user_role WHERE is_deleted = #{deleted} AND tenant_id = #{tenantId} AND role_id = #{roleId} LIMIT 1")
    String findUserIdByTenantIdAndRoleId(@Param("tenantId") String tenantId, @Param("roleId") String roleId, @Param("deleted") Object deleted);

}

