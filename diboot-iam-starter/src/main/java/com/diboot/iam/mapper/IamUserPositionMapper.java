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
import com.diboot.iam.entity.IamUserPosition;
import com.diboot.iam.vo.IamUserPositionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* 用户岗位关联Mapper
* @author mazc@dibo.ltd
* @version 2.2
* @date 2019-12-17
*/
@Mapper
public interface IamUserPositionMapper extends BaseCrudMapper<IamUserPosition> {

    @InterceptorIgnore(tenantLine = "true")
    @Select({"SELECT up.org_id AS orgId, up.position_id AS positionId, up.is_primary_position AS isPrimaryPosition," +
            " o.name as orgName, p.name as positionName, p.code as positionCode, p.data_permission_type AS dataPermissionType " +
            " FROM dbt_iam_user_position up " +
            " LEFT JOIN dbt_iam_org o ON up.org_id = o.id" +
            " LEFT JOIN dbt_iam_position p ON up.position_id=p.id" +
            " WHERE up.user_type = #{userType} AND up.user_id = #{userId} AND up.is_deleted = #{deleted}"})
    List<IamUserPositionVO> getUserPositions(@Param("userType")String userType, @Param("userId")String userId, @Param("deleted") Object deleted);

}

