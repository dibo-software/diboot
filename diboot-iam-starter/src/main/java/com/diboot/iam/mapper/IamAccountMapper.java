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
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.diboot.core.mapper.BaseCrudMapper;
import com.diboot.iam.entity.IamAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
* 认证用户Mapper
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-03
*/
@Mapper
public interface IamAccountMapper extends BaseCrudMapper<IamAccount> {

    /**
     * 查找登录用户
     *
     * @param queryWrapper
     * @param deleted
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select({"SELECT id, tenant_id, user_type, user_id, auth_account, auth_secret, secret_salt, status FROM dbt_iam_account WHERE is_deleted = #{deleted} AND ${ew.sqlSegment} LIMIT 1"})
    IamAccount findLoginAccount(@Param(Constants.WRAPPER) Wrapper<IamAccount> queryWrapper, @Param("deleted") Object deleted);

    /**
     * 重置密码
     *
     * @param id         账户id
     * @param authSecret 新密码
     * @return 是否更新成功
     */
    @InterceptorIgnore(tenantLine = "true")
    @Update("UPDATE dbt_iam_account SET auth_secret = #{authSecret} WHERE id = #{id}")
    boolean resetPassword(@Param("id") String id, @Param("authSecret") String authSecret);

    /**
     * 查找指定租户的用户账号
     *
     * @param tenantId
     * @param userId
     * @param userType
     * @param deleted
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select("SELECT * FROM dbt_iam_account WHERE is_deleted = #{deleted} AND tenant_id = #{tenantId} AND user_id = #{userId} AND user_type = #{userType} LIMIT 1")
    IamAccount findByExplicitTenant(@Param("tenantId") String tenantId, @Param("userId") String userId,
                                    @Param("userType") String userType, @Param("deleted") Object deleted);

    /**
     * 检查用户名是否重复
     *
     * @param tenantId
     * @param userType
     * @param username
     * @param userId
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select({"<script>SELECT count(*) FROM dbt_iam_account ",
            "WHERE is_deleted = #{deleted} AND tenant_id = #{tenantId} AND user_type = #{userType} AND auth_account = #{username}",
            "<if test='userId!=null'> AND user_id &lt;&gt; #{userId} </if></script>"})
    boolean checkUsernameDuplicate(@Param("tenantId") String tenantId, @Param("userType") String userType, @Param("username") String username,
                                   @Param("userId") String userId, @Param("deleted") Object deleted);

    /**
     * 更新租户管理员账号
     *
     * @param account
     * @param deleted
     */
    @InterceptorIgnore(tenantLine = "true")
    @Update({"<script> UPDATE dbt_iam_account SET auth_account = #{account.authAccount}, status = #{account.status}, update_time = #{account.updateTime}",
            "<if test='account.authSecret!=null'>, auth_secret = #{account.authSecret} <if test='account.secretSalt!=null'>, secret_salt = #{account.secretSalt} </if></if>",
            "WHERE tenant_id = #{account.tenantId} AND user_type = #{account.userType} AND user_id = #{account.userId} AND auth_type = #{account.authType} AND is_deleted = #{deleted} </script>"})
    boolean updateTenantAccount(@Param("account") IamAccount account, @Param("deleted") Object deleted);

}

