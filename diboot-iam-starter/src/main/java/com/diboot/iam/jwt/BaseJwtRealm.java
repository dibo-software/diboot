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
package com.diboot.iam.jwt;

import com.diboot.core.service.BaseService;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.V;
import com.diboot.core.vo.KeyValue;
import com.diboot.iam.auth.AuthService;
import com.diboot.iam.auth.AuthServiceFactory;
import com.diboot.iam.auth.IamExtensible;
import com.diboot.iam.cache.IamCacheManager;
import com.diboot.iam.config.Cons;
import com.diboot.iam.entity.BaseLoginUser;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.entity.IamRole;
import com.diboot.iam.service.IamRoleResourceService;
import com.diboot.iam.service.IamUserRoleService;
import com.diboot.iam.util.IamSecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
@Slf4j
public class BaseJwtRealm extends AuthorizingRealm {

    private IamUserRoleService iamUserRoleService;
    private IamRoleResourceService iamRoleResourceService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token != null && token instanceof BaseJwtAuthToken;
    }

    @Override
    public Class<?> getAuthenticationTokenClass() {
        return BaseJwtRealm.class;
    }

    /***
     * 获取认证信息
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        BaseJwtAuthToken jwtToken = (BaseJwtAuthToken) token;
        String authAccount = (String) jwtToken.getPrincipal();
        if (V.isEmpty(authAccount)){
            throw new AuthenticationException("无效的用户标识");
        }
        else {
            // 获取认证方式
            AuthService authService = AuthServiceFactory.getAuthService(jwtToken.getAuthType());
            if(authService == null){
                jwtToken.clearAuthtoken();
                throw new AuthenticationException("认证类型: "+jwtToken.getAuthType()+" 的AccountAuthService未实现！");
            }
            IamAccount account = authService.getAccount(jwtToken);
            // 登录失败则抛出相关异常
            if (account == null){
                jwtToken.clearAuthtoken();
                throw new AuthenticationException("用户账号或密码错误！");
            }
            // 获取当前user对象并缓存
            BaseLoginUser loginUser = null;
            BaseService userService = ContextHelper.getBaseServiceByEntity(jwtToken.getUserTypeClass());
            if(userService != null){
                loginUser = (BaseLoginUser)userService.getEntity(account.getUserId());
            }
            else{
                throw new AuthenticationException("用户 "+jwtToken.getUserTypeClass().getName()+" 相关的Service未定义！");
            }
            if(loginUser == null){
                throw new AuthenticationException("用户不存在");
            }
            loginUser.setAuthToken(jwtToken.getAuthtoken());
            IamExtensible iamExtensible = getIamUserRoleService().getIamExtensible();
            if(iamExtensible != null){
                KeyValue extentionObj = iamExtensible.getUserExtentionObj(jwtToken.getUserTypeClass().getSimpleName(), account.getUserId(), jwtToken.getExtObj());
                if(extentionObj != null){
                    loginUser.setExtentionObj(extentionObj);
                }
            }
            // 清空当前用户缓存
            this.clearCachedAuthorizationInfo(IamSecurityUtils.getSubject().getPrincipals());
            return new SimpleAuthenticationInfo(loginUser, jwtToken.getCredentials(), this.getName());
        }
    }

    /***
     * 获取授权信息
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        BaseLoginUser currentUser = (BaseLoginUser) principals.getPrimaryPrincipal();
        // 根据用户类型与用户id获取roleList
        Long extentionObjId = null;
        KeyValue extentionObj = currentUser.getExtentionObj();
        if(extentionObj != null){
            extentionObjId = (Long)extentionObj.getV();
        }
        // 获取角色列表
        List<IamRole> roleList = getIamUserRoleService().getUserRoleList(currentUser.getClass().getSimpleName(), currentUser.getId(), extentionObjId);
        // 如果没有任何角色，返回
        if (V.isEmpty(roleList)){
            return authorizationInfo;
        }
        // 整理所有角色许可列表
        Set<String> allRoleCodes = new HashSet<>();
        List<Long> roleIds = new ArrayList<>();
        roleList.stream().forEach(role->{
            // 添加当前角色到角色列表中
            allRoleCodes.add(role.getCode());
            roleIds.add(role.getId());
        });
        // 整理所有权限许可列表，从缓存匹配
        Set<String> allPermissionCodes = new HashSet<>();
        List<String> apiUrlList = getIamRoleResourceService().getApiUrlList(Cons.APPLICATION, roleIds);
        if(V.notEmpty(apiUrlList)){
            apiUrlList.stream().forEach(set->{
                for(String uri : set.split(Cons.SEPARATOR_COMMA)){
                    String permissionCode = IamCacheManager.getPermissionCode(uri);
                    if(permissionCode != null){
                        allPermissionCodes.add(permissionCode);
                    }
                }
            });
        }
        // 将所有角色和权限许可授权给用户
        authorizationInfo.setRoles(allRoleCodes);
        authorizationInfo.setStringPermissions(allPermissionCodes);
        return authorizationInfo;
    }

    private IamUserRoleService getIamUserRoleService(){
        if(iamUserRoleService == null){
            iamUserRoleService = ContextHelper.getBean(IamUserRoleService.class);
        }
        return iamUserRoleService;
    }

    private IamRoleResourceService getIamRoleResourceService(){
        if(iamRoleResourceService == null){
            iamRoleResourceService = ContextHelper.getBean(IamRoleResourceService.class);
        }
        return iamRoleResourceService;
    }

}
