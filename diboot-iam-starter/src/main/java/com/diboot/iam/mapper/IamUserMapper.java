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
import com.diboot.iam.entity.IamUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.io.Serializable;

/**
* 系统用户Mapper
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-17
*/
@Mapper
public interface IamUserMapper extends BaseCrudMapper<IamUser> {

    @InterceptorIgnore(tenantLine = "true")
    @Override
    IamUser selectById(Serializable id);

    /**
     * 更新租户管理员用户信息
     *
     * @param user
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    @Update({"UPDATE dbt_iam_user SET realname = #{user.realname}, gender = #{user.gender}, birthdate = #{user.birthdate}, ",
            "mobile_phone = #{user.mobilePhone}, email = #{user.email}, status = #{user.status} ",
            "WHERE id = #{user.id} AND is_deleted = #{deleted}"})
    boolean updateTenantAdmin(@Param("user") IamUser user, @Param("deleted") Object deleted);
}