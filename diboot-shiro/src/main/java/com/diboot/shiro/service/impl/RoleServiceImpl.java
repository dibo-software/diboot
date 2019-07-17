package com.diboot.shiro.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.binding.manager.AnnotationBindingManager;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.core.vo.Pagination;
import com.diboot.shiro.entity.Permission;
import com.diboot.shiro.entity.Role;
import com.diboot.shiro.entity.RolePermission;
import com.diboot.shiro.entity.UserRole;
import com.diboot.shiro.mapper.RoleMapper;
import com.diboot.shiro.service.PermissionService;
import com.diboot.shiro.service.RolePermissionService;
import com.diboot.shiro.service.RoleService;
import com.diboot.shiro.service.UserRoleService;
import com.diboot.shiro.vo.PermissionVO;
import com.diboot.shiro.vo.RoleVO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 角色相关Service
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
@Service
@Slf4j
public class RoleServiceImpl extends BaseServiceImpl<RoleMapper, Role> implements RoleService {
    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private RolePermissionService rolePermissionService;

    @Override
    public List<RoleVO> getRoleList(Wrapper queryWrapper, Pagination pagination) {
        List<Role> roleList = super.getEntityList(queryWrapper, pagination);
        List<RoleVO> roleVOList = AnnotationBindingManager.autoConvertAndBind(roleList, RoleVO.class);
        if(V.notEmpty(roleVOList)){
            for(RoleVO roleVO : roleVOList){
                List<Permission> permissionList = roleVO.getPermissionList();
                if(V.notEmpty(permissionList)){
                    //获取这个角色拥有的菜单资源,并去重
                    List<Permission> menuList = new ArrayList();//菜单资源
                    HashSet menuSet = new HashSet();
                    Set<Long> idSet = new HashSet<>();//资源id  set
                    for(Permission permission : permissionList){
                        //克隆roleVO.permissionList中的每一个permission，解决查出来的列表页数据重复的问题
                        Permission temp = BeanUtils.convert(permission, Permission.class);
                        idSet.add(temp.getId());
                        if(menuSet.add(temp.getMenuCode())){
                            menuList.add(temp);
                        }
                    }
                    //获取菜单资源下的该角色已有的权限资源
                    if(V.notEmpty(menuList)){
                        for(Permission menu : menuList){
                            QueryWrapper<Permission> query = new QueryWrapper();
                            query.lambda().in(Permission::getId, idSet)
                                          .eq(Permission::getMenuCode, menu.getMenuCode());
                            List<Permission> menuPermissionList = permissionService.getEntityList(query);
                            menu.setPermissionList(menuPermissionList);
                        }
                    }

                    roleVO.setMenuList(menuList);
                }
            }
        }

        return roleVOList;
    }



    @Override
    public RoleVO getRole(Long id) {
        RoleVO roleVO = super.getViewObject(id, RoleVO.class);
        //角色已拥有的权限列表
        List<Permission> ownPermissionList = roleVO.getPermissionList();
        //角色已拥有的菜单资源
        List<Permission> ownMenuList = null;
        Set<Long> idSet = new HashSet<>();
        if(V.notEmpty(ownPermissionList)){
            //获取这个角色拥有的菜单资源
            ownMenuList = new ArrayList();
            HashSet set = new HashSet();
            for(Permission permission : ownPermissionList){
                idSet.add(permission.getId());
                if(set.add(permission.getMenuCode())){
                    ownMenuList.add(permission);
                }
            }
        }

        //获取菜单资源下的该角色已有的权限资源
        if(V.notEmpty(ownMenuList)){
            for(Permission menu : ownMenuList){
                QueryWrapper<Permission> query = new QueryWrapper();
                query.lambda().in(Permission::getId, idSet)
                        .eq(Permission::getMenuCode, menu.getMenuCode());
                List<Permission> menuPermissionList = permissionService.getEntityList(query);
                menu.setPermissionList(menuPermissionList);
            }
        }

        roleVO.setMenuList(ownMenuList);

        return roleVO;
    }

    @Override
    @Transactional
    public boolean createRole(Role role) {
        if(V.isEmpty(role)){
            return false;
        }

        try{
            //新建角色信息
            boolean success = super.createEntity(role);
            if(!success){
                return false;
            }
            //新建角色权限信息
            List<Permission> permissionList = role.getPermissionList();
            if(V.notEmpty(permissionList)){
                for(Permission p : permissionList){
                    RolePermission rolePermission = new RolePermission();
                    rolePermission.setRoleId(role.getId());
                    rolePermission.setPermissionId(p.getId());
                    rolePermissionService.createEntity(rolePermission);
                }
            }
        }catch(Exception e){
            throw new RuntimeException();
        }

        return true;
    }

    @Override
    public RoleVO toUpdatePage(Long id) {
        RoleVO roleVO = super.getViewObject(id, RoleVO.class);
        //角色已拥有的权限列表
        List<Permission> ownPermissionList = roleVO.getPermissionList();
        //角色已拥有的菜单资源
        List<Permission> ownMenuList = null;
        if(V.notEmpty(ownPermissionList)){
            //获取这个角色拥有的菜单资源
            ownMenuList = new ArrayList();
            HashSet set = new HashSet();
            for(Permission permission : ownPermissionList){
                if(set.add(permission.getMenuCode())){
                    ownMenuList.add(permission);
                }
            }
        }

        //获取所有菜单及菜单下的权限信息
        List<Permission> allMenuList = getAllMenu();

        if(V.notEmpty(allMenuList)){
            for(Permission menu : allMenuList){
                List<Permission> allPermissionList = menu.getPermissionList();
                //判断该角色是否有该菜单资源，若有设为true
                if(V.notEmpty(ownMenuList)){
                    for(Permission m : ownMenuList){
                        if(menu.getMenuCode().equals(m.getMenuCode())){
                            menu.setOwn(true);
                        }
                    }
                }
                //判断该角色是否有该资源权限，若有设为true
                if(V.notEmpty(allPermissionList) && V.notEmpty(ownPermissionList)){
                    for(Permission permission : allPermissionList){
                        for(Permission p : ownPermissionList){
                            if(permission.getId().equals(p.getId())){
                                permission.setOwn(true);
                            }
                        }
                    }
                }
            }
        }

        roleVO.setMenuList(allMenuList);

        return roleVO;
    }

    @Override
    @Transactional
    public boolean updateRole(Role role) {
        if(V.isEmpty(role)){
            return false;
        }
        try {
            //更新角色信息
            boolean success = super.updateEntity(role);
            if(!success){
                return false;
            }
            //获取角色原来拥有的权限信息
            QueryWrapper<RolePermission> query = new QueryWrapper();
            query.lambda().eq(RolePermission::getRoleId, role.getId());
            List<RolePermission> oldPermissionList = rolePermissionService.getEntityList(query);

            List<Permission> newPermissionList = role.getPermissionList();
            StringBuffer oldBuffer = new StringBuffer();
            StringBuffer newBuffer = new StringBuffer();
            if(V.notEmpty(oldPermissionList)){
                for(RolePermission rp : oldPermissionList){
                    oldBuffer.append(rp.getPermissionId()).append(",");
                }
            }
            if(V.notEmpty(newPermissionList)){
                for(Permission p : newPermissionList){
                    newBuffer.append(p.getId()).append(",");
                }
            }

            //删除页面取消选择的角色权限
            if(V.notEmpty(oldPermissionList)){
                for(RolePermission rp : oldPermissionList){
                    if(!(newBuffer.toString().contains(rp.getPermissionId().toString()))){
                        rolePermissionService.deleteEntity(rp.getId());
                    }
                }
            }

            //新增页面选择的角色权限
            if(V.notEmpty(newPermissionList)){
                for(Permission p : newPermissionList){
                    if(!(oldBuffer.toString().contains(p.getId().toString()))){
                        RolePermission entity = new RolePermission();
                        entity.setRoleId(role.getId());
                        entity.setPermissionId(p.getId());
                        rolePermissionService.createEntity(entity);
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException();
        }

        return true;
    }

    @Override
    @Transactional
    public boolean deleteRole(Long id) {
        try {
            boolean success = super.deleteEntity(id);
            if(!success){
                return false;
            }
            //获取角色原来拥有的权限信息
            QueryWrapper<RolePermission> query = new QueryWrapper();
            query.lambda().eq(RolePermission::getRoleId, id);
            List<RolePermission> rolePermissionList = rolePermissionService.getEntityList(query);
            //删除角色权限
            if(V.notEmpty(rolePermissionList)){
                for(RolePermission rp : rolePermissionList){
                    rolePermissionService.deleteEntity(rp.getId());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }

        return true;
    }

    @Override
    public List<Permission> getAllMenu() {
        //获取所有菜单
        Wrapper wrapper = new QueryWrapper<Permission>()
                .lambda()
                .groupBy(Permission::getMenuCode)
                .select()
                ;
        List<Permission> menuList = permissionService.getEntityList(wrapper);

        if(V.notEmpty(menuList)){
            for(Permission menu : menuList){
                //获取一个菜单的所有权限资源
                QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(Permission::getMenuCode, menu.getMenuCode());
                List<Permission> allPermissionList = permissionService.getEntityList(queryWrapper);
                menu.setPermissionList(allPermissionList);
            }
        }

        return menuList;
    }

    @Override
    public List<RoleVO> getRelatedRoleAndPermissionListByUser(String userType, Long userId) {
        // 根据用户类型与用户id获取roleList
        QueryWrapper<UserRole> query = new QueryWrapper<>();
        query.lambda()
                .eq(UserRole::getUserType, userType)
                .eq(UserRole::getUserId, userId);
        List<UserRole> userRoleList = userRoleService.getEntityList(query);
        if (V.isEmpty(userRoleList)){
            return Collections.emptyList();
        }
        List<Long> roleIdList = userRoleList.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
        if (V.isEmpty(roleIdList)){
            return Collections.emptyList();
        }

        // 获取角色列表，并使用VO自动多对多关联permission
        QueryWrapper<Role> roleQuery = new QueryWrapper<>();
        roleQuery
                .lambda()
                .in(Role::getId, roleIdList);
        List<RoleVO> roleVOList = this.getViewObjectList(roleQuery, null, RoleVO.class);
        return roleVOList;
    }
}
