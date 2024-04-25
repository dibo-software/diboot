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
package com.diboot.iam.shiro;

import com.diboot.core.service.BaseService;
import com.diboot.core.util.ContextHolder;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.LabelValue;
import com.diboot.iam.auth.AuthService;
import com.diboot.iam.auth.AuthServiceFactory;
import com.diboot.iam.auth.IamExtensible;
import com.diboot.iam.auth.IamTenantPermission;
import com.diboot.iam.config.Cons;
import com.diboot.iam.entity.BaseLoginUser;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.entity.IamRole;
import com.diboot.iam.service.IamRoleResourceService;
import com.diboot.iam.service.IamUserRoleService;
import com.diboot.iam.util.IamSecurityUtils;
import com.diboot.iam.vo.PositionDataScope;
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
 * IAM realm定义
 * @author JerryMa
 * @version v2.6.0
 * @date 2022/4/26
 * Copyright © diboot.com
 */
@Slf4j
public class IamAuthorizingRealm extends AuthorizingRealm {

    private IamUserRoleService iamUserRoleService;
    private IamRoleResourceService iamRoleResourceService;
    private IamTenantPermission iamTenantPermission;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token != null && token instanceof IamAuthToken;
    }

    /***
     * 获取认证信息
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        IamAuthToken iamAuthToken = (IamAuthToken) token;
        String authAccount = (String) iamAuthToken.getPrincipal();
        if (V.isEmpty(authAccount)){
            throw new AuthenticationException("无效的用户标识");
        }
        else {
            // 获取认证方式
            AuthService authService = AuthServiceFactory.getAuthService(iamAuthToken.getAuthType());
            if(authService == null){
                iamAuthToken.clearAuthtoken();
                throw new AuthenticationException("认证类型: "+iamAuthToken.getAuthType()+" 的AccountAuthService未实现！");
            }
            IamAccount account = authService.getAccount(iamAuthToken);
            // 登录失败则抛出相关异常
            if (account == null){
                iamAuthToken.clearAuthtoken();
                throw new AuthenticationException("用户账号或密码错误！");
            }
            // 获取当前user对象并缓存
            BaseLoginUser loginUser = null;
            BaseService userService = ContextHolder.getBaseServiceByEntity(iamAuthToken.getUserTypeClass());
            if(userService != null){
                loginUser = (BaseLoginUser)userService.getEntity(account.getUserId());
            }
            else{
                throw new AuthenticationException("用户 "+iamAuthToken.getUserTypeClass().getName()+" 相关的Service未定义！");
            }
            if(loginUser == null){
                throw new AuthenticationException("用户不存在");
            }
            loginUser.setAuthToken(iamAuthToken.getAuthtoken());
            IamExtensible iamExtensible = getIamUserRoleService().getIamExtensible();
            if(iamExtensible != null){
                LabelValue extensionObj = iamExtensible.getUserExtensionObj(iamAuthToken.getUserTypeClass().getSimpleName(), account.getUserId(), iamAuthToken.getExtObj());
                if(extensionObj != null){
                    loginUser.setExtensionObj(extensionObj);
                }
            }
            // 清空当前用户缓存
            this.clearCachedAuthorizationInfo(IamSecurityUtils.getSubject().getPrincipals());
            log.debug("获取用户认证信息完成 : {}", iamAuthToken.getCredentials());
            return new SimpleAuthenticationInfo(loginUser, iamAuthToken.getCredentials(), new CustomSimpleByteSource(), this.getName());
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
        String extensionObjId = null;
        LabelValue extensionObj = currentUser.getExtensionObj();
        if(extensionObj != null){
            if(extensionObj.getExt() != null && extensionObj.getExt() instanceof PositionDataScope){
                extensionObjId = ((PositionDataScope)extensionObj.getExt()).getPositionId();
            }
        }
        // 获取角色列表
        List<IamRole> roleList = getIamUserRoleService().getUserRoleList(currentUser.getTenantId(), currentUser.getClass().getSimpleName(), currentUser.getId(), extensionObjId);
        // 如果没有任何角色，返回
        if (V.isEmpty(roleList)){
            return authorizationInfo;
        }
        // 整理所有角色许可列表
        Set<String> allRoleCodes = new HashSet<>();
        List<String> roleIds = new ArrayList<>(roleList.size());
        roleList.stream().forEach(role->{
            // 添加当前角色到角色列表中
            allRoleCodes.add(role.getCode());
            roleIds.add(role.getId());
        });
        authorizationInfo.setRoles(allRoleCodes);
        // 整理所有权限许可列表，从缓存匹配
        List<String> allPermissionCodes = null;
        if (allRoleCodes.contains(Cons.ROLE_TENANT_ADMIN)) {
            allPermissionCodes = getIamTenantPermission().findAllPermissionCodes(currentUser.getTenantId());
        } else {
            allPermissionCodes = getIamRoleResourceService().getPermissionCodeList(Cons.APPLICATION, roleIds);
        }
        Set<String> permissionCodesSet = new HashSet<>();
        if(V.notEmpty(allPermissionCodes)){
            allPermissionCodes.forEach(permCodeStr -> {
                if(!permCodeStr.contains(Cons.SEPARATOR_COMMA)){
                    permissionCodesSet.add(permCodeStr);
                }
                else{
                    permissionCodesSet.addAll(S.splitToList(permCodeStr));
                }
            });
        }
        // 将所有角色和权限许可授权给用户
        authorizationInfo.setStringPermissions(permissionCodesSet);
        log.debug("获取用户授权信息完成 : {}", currentUser.getDisplayName());
        return authorizationInfo;
    }

    private IamUserRoleService getIamUserRoleService(){
        if(iamUserRoleService == null){
            iamUserRoleService = ContextHolder.getBean(IamUserRoleService.class);
        }
        return iamUserRoleService;
    }

    private IamRoleResourceService getIamRoleResourceService(){
        if(iamRoleResourceService == null){
            iamRoleResourceService = ContextHolder.getBean(IamRoleResourceService.class);
        }
        return iamRoleResourceService;
    }

    private IamTenantPermission getIamTenantPermission(){
        if(iamTenantPermission == null){
            iamTenantPermission = ContextHolder.getBean(IamTenantPermission.class);
        }
        return iamTenantPermission;
    }

}
