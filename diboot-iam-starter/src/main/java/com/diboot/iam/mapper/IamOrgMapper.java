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
package com.diboot.iam.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.diboot.core.mapper.BaseCrudMapper;
import com.diboot.iam.entity.IamOrg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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

}

