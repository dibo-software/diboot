package com.diboot.iam.jwt;

import com.diboot.core.service.BaseService;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.V;
import com.diboot.iam.auth.AuthService;
import com.diboot.iam.auth.AuthServiceFactory;
import com.diboot.iam.config.Cons;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.entity.IamPermission;
import com.diboot.iam.entity.IamRole;
import com.diboot.iam.service.IamRolePermissionService;
import com.diboot.iam.service.IamUserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Autowired
    private IamUserRoleService iamUserRoleService;
    @Autowired
    private IamRolePermissionService iamRolePermissionService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token != null && token instanceof BaseJwtAuthToken;
    }

    @Override
    public Class<?> getAuthenticationTokenClass() {
        return BaseJwtRealm.class;
    }

    /***
     * 认证
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
            Object userObject = null;
            BaseService userService = ContextHelper.getBaseServiceByEntity(jwtToken.getUserTypeClass());
            if(userService != null){
                userObject = userService.getEntity(account.getUserId());
            }
            else{
                throw new AuthenticationException("用户 "+jwtToken.getUserTypeClass().getName()+" 相关的Service未定义！");
            }
            if(userObject == null){
                throw new AuthenticationException("用户不存在");
            }
            return new SimpleAuthenticationInfo(userObject, jwtToken.getCredentials(), this.getName());
        }
    }

    /***
     * 授权
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        Object currentUser = principals.getPrimaryPrincipal();
        // 根据用户类型与用户id获取roleList
        Long userId = (Long)BeanUtils.getProperty(currentUser, Cons.FieldName.id.name());
        List<IamRole> roleList = iamUserRoleService.getUserRoleList(currentUser.getClass().getSimpleName(), userId);
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
        // 整理所有权限许可列表
        Set<String> allPermissionCodes = new HashSet<>();
        List<IamPermission> permissionList = iamRolePermissionService.getPermissionList(Cons.APPLICATION, roleIds);
        if(V.notEmpty(permissionList)){
            permissionList.stream().forEach(p->{
                if(V.notEmpty(p.getOperationCode())){
                    allPermissionCodes.add(p.getOperationCode());
                }
                else if(V.equals(p.getParentId(), 0L) && V.notEmpty(p.getCode())){
                    allPermissionCodes.add(p.getCode());
                }
            });
        }
        // 将所有角色和权限许可授权给用户
        authorizationInfo.setRoles(allRoleCodes);
        authorizationInfo.setStringPermissions(allPermissionCodes);
        return authorizationInfo;
    }

}
