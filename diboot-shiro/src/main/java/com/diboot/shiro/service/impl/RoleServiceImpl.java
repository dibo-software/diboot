package com.diboot.shiro.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.binding.RelationsBinder;
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
        List<RoleVO> roleVOList = RelationsBinder.convertAndBind(roleList, RoleVO.class);
        if(V.notEmpty(roleVOList)){
            for(RoleVO roleVO : roleVOList){
                List<Permission> permissionList = roleVO.getPermissionList();
                if(V.notEmpty(permissionList)){
                    //获取这个角色拥有的菜单资源,并去重
                    List<Permission> menuList = new ArrayList();//菜单资源
                    Set<String> menuCodeSet = new HashSet<>();
                    for(Permission permission : permissionList){
                        if(menuCodeSet.add(permission.getMenuCode())){
                            menuList.add(permission);
                        }
                    }
                    //获取菜单资源下的该角色已有的权限资源
                    if(V.notEmpty(menuList)){
                        for(Permission menu : menuList){
                            List<Permission> menuPermissionList = new ArrayList<>();
                            for(Permission p : permissionList){
                                if(menu.getMenuCode().equals(p.getMenuCode())){
                                    //对象转化一下，解决死循环的问题
                                    Permission permission = BeanUtils.convert(p, Permission.class);
                                    menuPermissionList.add(permission);
                                }
                            }
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
        //获取菜单资源下的该角色已有的权限资源
        if(V.notEmpty(ownMenuList)){
            for(Permission menu : ownMenuList){
               List<Permission> menuPermissionList = new ArrayList<>();
               for(Permission permission : ownPermissionList){
                   if(menu.getMenuCode().equals(permission.getMenuCode())){
                       Permission p = BeanUtils.convert(permission, Permission.class);
                       menuPermissionList.add(p);
                   }
               }
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
                List<RolePermission> rpList = new ArrayList<>();
                for(Permission p : permissionList){
                    RolePermission rolePermission = new RolePermission();
                    rolePermission.setRoleId(role.getId());
                    rolePermission.setPermissionId(p.getId());
                    rpList.add(rolePermission);
                }
                if(V.notEmpty(rpList)){
                    rolePermissionService.createEntities(rpList);
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
        //角色已拥有的资源列表
        List<Permission> ownPermissionList = roleVO.getPermissionList();
        //角色已拥有的菜单资源
        List<Permission> ownMenuList = new ArrayList();
        HashSet<String> ownMenuSet = new HashSet();
        if(V.notEmpty(ownPermissionList)){
            //获取这个角色拥有的菜单资源
            for(Permission permission : ownPermissionList){
                if(ownMenuSet.add(permission.getMenuCode())){
                    ownMenuList.add(permission);
                }
            }
        }
        //获取菜单资源下的该角色已有的权限资源
        Map<String, Permission> ownMenuMap = new HashMap();
        if(V.notEmpty(ownMenuList)){
            for(Permission menu : ownMenuList){
                List<Permission> menuPermissionList = new ArrayList<>();
                for(Permission permission : ownPermissionList){
                    if(menu.getMenuCode().equals(permission.getMenuCode())){
                        Permission p = BeanUtils.convert(permission, Permission.class);
                        menuPermissionList.add(p);
                    }
                }
                menu.setPermissionList(menuPermissionList);
                ownMenuMap.put(menu.getMenuCode(), menu);
            }
        }
        //获取所有菜单及菜单下的资源信息
        List<Permission> allMenuList = getAllMenu();
        if(V.notEmpty(allMenuList)){
            for(Permission menu : allMenuList){
               if(ownMenuSet.contains(menu.getMenuCode())){
                   Permission ownMenu = ownMenuMap.get(menu.getMenuCode());
                   List<Permission> allPermissionList = menu.getPermissionList();
                   int ownPermissionCount = 0;
                   if(V.notEmpty(allPermissionList) && V.notEmpty(ownMenu.getPermissionList())){
                       for(Permission permission : allPermissionList){
                           for(Permission p : ownMenu.getPermissionList()){
                               if(permission.getId().equals(p.getId())){
                                   permission.setOwn(true);
                                   ownPermissionCount++;
                                   break;
                               }
                           }
                       }
                   }
                   if(ownPermissionCount == allPermissionList.size()){
                       menu.setChecked(true);
                   }
                   if(ownPermissionCount != 0 && ownPermissionCount < allPermissionList.size()){
                       menu.setIndeterminate(true);
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
                List<RolePermission> rpList = new ArrayList<>();
                for(Permission p : newPermissionList){
                    if(!(oldBuffer.toString().contains(p.getId().toString()))){
                        RolePermission entity = new RolePermission();
                        entity.setRoleId(role.getId());
                        entity.setPermissionId(p.getId());
                        rpList.add(entity);
                    }
                }
                if(V.notEmpty(rpList)){
                    rolePermissionService.createEntities(rpList);
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
            //删除角色原来拥有的权限信息
            QueryWrapper<RolePermission> query = new QueryWrapper();
            query.lambda().eq(RolePermission::getRoleId, id);
            rolePermissionService.deleteEntities(query);
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
                ;
        List<Permission> menuList = permissionService.getEntityList(wrapper);
        //获取所有资源
        List<Permission> permissionList = permissionService.getEntityList(null);
        //把资源根据菜单分类
        if(V.notEmpty(menuList)){
            for(Permission menu : menuList){
                if(V.notEmpty(permissionList)){
                    List<Permission> menuPermissionList = new ArrayList<>();
                    for(Permission permission : permissionList){
                        if(menu.getMenuCode().equals(permission.getMenuCode())){
                            menuPermissionList.add(permission);
                        }
                    }
                    menu.setPermissionList(menuPermissionList);
                }
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
