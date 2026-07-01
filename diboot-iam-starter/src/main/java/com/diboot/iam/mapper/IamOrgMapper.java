/*
 * Copyright (c) 2015-2099, www.dibo.ltd (service@dibo.ltd).
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
import com.diboot.iam.entity.IamOrg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 组织机构Mapper
 *
 * @author mazc@dibo.ltd
 * @version 2.2
 * @date 2019-12-03
 */
@Mapper
public interface IamOrgMapper extends BaseCrudMapper<IamOrg> {

    /**
     * 查询租户的根部门id
     *
     * @param tenantId
     * @param deleted
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select({"SELECT id FROM dbt_iam_org WHERE is_deleted = #{deleted} AND tenant_id = #{tenantId} AND (parent_id = '0' or parent_id is null)"})
    String getTenantRootOrgId(String tenantId, Object deleted);

    /**
     * 获取某负责人负责的相关部门ids
     * @param managerId 负责人id
     * @param deleted
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select({"SELECT id FROM dbt_iam_org WHERE manager_id = #{managerId} AND is_deleted = #{deleted}"})
    List<String> getOrgIdsByManagerId(@Param("managerId") String managerId, @Param("deleted") Object deleted);

    /**
     * 根据id获取部门
     * @param id
     * @param deleted
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select({"SELECT id, parent_id, parent_ids_path, name FROM dbt_iam_org WHERE id = #{id} AND is_deleted = #{deleted}"})
    IamOrg getOrgById(@Param("id") String id, @Param("deleted") Object deleted);

    /**
     * 根据上级路径获取部门ids
     * @param parentIdsPath
     * @param deleted
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select({"SELECT id FROM dbt_iam_org WHERE parent_ids_path like #{parentIdsPath} AND is_deleted = #{deleted} ORDER BY sort_id ASC"})
    List<String> getOrgIdsByPath(@Param("parentIdsPath") String parentIdsPath, @Param("deleted") Object deleted);

    /**
     * 获取相关部门ids
     * @param parentId 上级id
     * @param deleted
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select({"SELECT id FROM dbt_iam_org WHERE parent_id = #{parentId} AND is_deleted = #{deleted} ORDER BY sort_id ASC"})
    List<String> getOrgIdsByParentId(@Param("parentId") String parentId, @Param("deleted") Object deleted);

}

