package com.diboot.shiro.jwt;

import com.diboot.core.entity.BaseEntity;
import com.diboot.core.util.V;
import com.diboot.shiro.entity.Permission;
import com.diboot.shiro.service.*;
import com.diboot.shiro.vo.RoleVO;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
public class BaseJwtRealm extends AuthorizingRealm {

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleService roleService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token != null && token instanceof BaseJwtAuthenticationToken;
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
        BaseJwtAuthenticationToken jwtToken = (BaseJwtAuthenticationToken) token;

        String account = (String) jwtToken.getPrincipal();

        if (V.isEmpty(account)){
            throw new AuthenticationException("无效的token");
        }
        else {
            // 获取认证方式
            AuthWayService authWayService = jwtToken.getAuthWayService();

            BaseEntity user = authWayService.getUser();

            // 登录失败则抛出相关异常
            if (user == null){
                throw new AuthenticationException("用户不存在");
            }

            if (authWayService.requirePassword() && !authWayService.isPasswordMatch()){
                throw new AuthenticationException("用户名或密码错误");
            }
            return new SimpleAuthenticationInfo(user, jwtToken.getCredentials(), this.getName());
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

        // 获取用户类型
        String userType = principals.getPrimaryPrincipal().getClass().getSimpleName();
        BaseEntity user = (BaseEntity) principals.getPrimaryPrincipal();

        // 根据用户类型与用户id获取roleList
        List<RoleVO> roleVOList = roleService.getRelatedRoleAndPermissionListByUser(userType, user.getId());

        if (V.isEmpty(roleVOList)){
            return authorizationInfo;
        }

        // 整理所有权限许可列表
        Set<String> allStringRoleList = new HashSet<>();
        Set<String> allStringPermissionList = new HashSet<>();
        for (RoleVO roleVO : roleVOList){
            // 添加当前角色到角色列表中
            allStringRoleList.add(roleVO.getCode());

            if (V.isEmpty(roleVO.getPermissionList())){
                continue;
            }

            // 添加当前所有权限许可到权限许可列表
            List stringPermissionList = roleVO.getPermissionList().stream()
                    .map(Permission::getPermissionCode)
                    .collect(Collectors.toList());
            allStringPermissionList.addAll(stringPermissionList);
        }

        // 将所有角色和权限许可授权给用户
        authorizationInfo.setRoles(allStringRoleList);
        authorizationInfo.setStringPermissions(allStringPermissionList);

        return authorizationInfo;
    }

}
