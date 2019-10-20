package com.diboot.shiro.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.binding.RelationsBinder;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import com.diboot.shiro.authz.config.SystemParamConfig;
import com.diboot.shiro.dto.RoleDto;
import com.diboot.shiro.entity.*;
import com.diboot.shiro.enums.IUserType;
import com.diboot.shiro.exception.ShiroCustomException;
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
    private PermissionService permissionService;

    @Autowired
    private RolePermissionService rolePermissionService;

    @Autowired
    private SystemParamConfig systemParamConfig;

    @Override
    public List<RoleVO> getRoleList(Wrapper queryWrapper, Pagination pagination) {
        List<Role> roleList = super.getEntityList(queryWrapper, pagination);
        this.buildRoleAndMenuPermissionList(roleList);
        return BeanUtils.convertList(roleList, RoleVO.class);
    }

    @Override
    public RoleVO getRole(Long id) {
        Role role = super.getEntity(id);
        this.buildRoleAndMenuPermissionList(Arrays.asList(role));;
        return BeanUtils.convert(role, RoleVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createRole(Role role) {
        if(V.isEmpty(role)){
            return false;
        }
        //新建角色信息
        if(!super.createEntity(role)){
            throw new ShiroCustomException(Status.FAIL_OPERATION, "创建角色失败！");
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
            if (!rolePermissionService.createEntities(rpList)) {
                throw new ShiroCustomException(Status.FAIL_OPERATION, "创建角色失败！");
            }
        }
        return true;
    }

    @Override
    public RoleVO toUpdatePage(Long id) {
        LambdaQueryWrapper<Role> roleQueryWrapper = Wrappers.<Role>lambdaQuery().eq(Role::getId, id);
        RoleVO roleVO = BeanUtils.convert(this.getOne(roleQueryWrapper), RoleVO.class);
        List<Permission> ownPermissionList = permissionService.getPermissionListByRoleIdList(Arrays.asList(roleVO.getId()));
        //角色已拥有的资源列表
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
        roleVO.setPermissionList(ownPermissionList);
        //获取所有菜单及菜单下的资源信息
        List<Permission> allMenuList = permissionService.getApplicationAllPermissionList();
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
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(Role role) {
        //更新角色信息
        if(!super.updateEntity(role)){
            return false;
        }
        //组装需要删除 or 创建的（角色-权限）
        List<RolePermission> createOrDeleteRolePermissionList = new ArrayList<>();
        this.buildCreateOrDeleteRolePermissionList(role, createOrDeleteRolePermissionList);
        if (V.notEmpty(createOrDeleteRolePermissionList)) {
            boolean success = rolePermissionService.createOrUpdateOrDeleteEntities(createOrDeleteRolePermissionList, createOrDeleteRolePermissionList.size());
            if (!success) {
                throw new ShiroCustomException(Status.FAIL_VALIDATION, "更新角色失败！");
            }
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(Long id) {
        if(!super.deleteEntity(id)){
            throw new ShiroCustomException(Status.FAIL_VALIDATION, "删除角色失败！");
        }
        //物理删除角色的权限的信息
        Map<String, Object> criteria = new HashMap(2){{
            put("roleId", id);
        }};
        if(!rolePermissionService.deletePhysics(criteria)){
            throw new ShiroCustomException(Status.FAIL_VALIDATION, "删除角色失败！");
        }
        return true;
    }

    @Override
    public List<RoleVO> getSysUserRelRole(List<Long> sysUserIdList) {
        // 1.1 获取账户对应的角色信息
        List<RoleVO> roleVOList = getRoleByUserIdList(sysUserIdList);
        if (V.notEmpty(roleVOList)) {
            List<Long> roleIdList = roleVOList.stream().map(roleVO -> roleVO.getId()).collect(Collectors.toList());
            //1.2 获取角色对应的权限
            List<Permission> permissionList =  permissionService.getPermissionListByRoleIdList(roleIdList);
            //1.3 构建角色和权限的关系
            Map<Long, List<Permission>> roleIdPermissionListMap = new HashMap<>(128);
            if (V.notEmpty(permissionList)) {
                permissionList.stream().forEach(permission -> {
                    List<Permission> roleIdPermissionList = roleIdPermissionListMap.get(permission.getRoleId());
                    if (V.isEmpty(roleIdPermissionList)) {
                        roleIdPermissionList = new ArrayList<>();
                    }
                    roleIdPermissionList.add(permission);
                    roleIdPermissionListMap.put(permission.getRoleId(), roleIdPermissionList);
                });
                //1.4 绑定角色和权限，构建账户和角色关系
                //判断用户是不是管理员
                roleVOList.stream().forEach(roleVO -> {
                    //1.4.1 绑定角色和权限关系
                    roleVO.setPermissionList(roleIdPermissionListMap.get(roleVO.getId()));
                });
            }
        }
        return roleVOList;
    }

    @Override
    public List<RoleVO> getRoleByUserIdList(List<Long> userIdList) {
        return this.getBaseMapper().getRoleByUserIdList(userIdList);
    }

    /**
     * 构建角色信息
     * @param roleList
     */
    private void buildRoleAndMenuPermissionList(List<Role> roleList) {
        if (V.notEmpty(roleList)) {
            List<Long> roleIdList = roleList.stream().map(Role::getId).collect(Collectors.toList());
            List<Permission> permissionList = permissionService.getPermissionListByRoleIdList(roleIdList);
            //角色-菜单Map
            Map<Long, Map<String, Permission>> roleIdAndMenuPermissionMap = new HashMap<>(16);
            if (V.notEmpty(permissionList)) {
                //1、根据菜单去重，组建菜单
                Map<String, List<Permission>> subMenuPermissionListMap = new HashMap<>(255);
                for (Permission permission : permissionList) {
                    //菜单权限
                    Map<String, Permission> menuCodePermissionMap = roleIdAndMenuPermissionMap.get(permission.getRoleId());
                    menuCodePermissionMap = V.isEmpty(menuCodePermissionMap) ? new HashMap<>(32) : menuCodePermissionMap;
                    //存放菜单子项权限
                    List<Permission> subMenuPermissionList = subMenuPermissionListMap.get(S.join(permission.getMenuCode(), permission.getRoleId()));
                    subMenuPermissionList = V.isEmpty(subMenuPermissionList) ? new ArrayList<>() : subMenuPermissionList;
                    subMenuPermissionList.add(permission);
                    subMenuPermissionListMap.put(S.join(permission.getMenuCode(), permission.getRoleId()), subMenuPermissionList);
                    //将子权限放入父菜单
                    Permission copyPermission = BeanUtils.convert(permission, Permission.class);
                    copyPermission.setPermissionList(subMenuPermissionList);
                    menuCodePermissionMap.put(copyPermission.getMenuCode(), copyPermission);
                    //构建角色 -菜单权限 - 子项权限
                    roleIdAndMenuPermissionMap.put(permission.getRoleId(), menuCodePermissionMap);
                }
            }
            //设置 角色-权限
            roleList.stream()
                    .forEach(role -> {
                        if (V.notEmpty(systemParamConfig.getAuth())) {
                            if (systemParamConfig.getAuth().contains(role.getCode())) {
                                role.setAdmin(true);
                            }
                        } else {
                            if ("admin".equalsIgnoreCase(role.getCode())) {
                                role.setAdmin(true);
                            }
                        }
                        Map<String, Permission> menuCodePermissionMap = roleIdAndMenuPermissionMap.get(role.getId());
                        if (V.notEmpty(menuCodePermissionMap)) {
                            role.setPermissionList(menuCodePermissionMap.values().stream().collect(Collectors.toList()));
                        }
                    });
        }
    }

    /**
     * 构建需要创建或者删除的（角色-权限）集合
     * @param role
     * @param createOrDeleteRolePermissionList
     */
    private void buildCreateOrDeleteRolePermissionList(Role role, List<RolePermission> createOrDeleteRolePermissionList) {
        //获取角色原来拥有的权限信息
        QueryWrapper<RolePermission> query = new QueryWrapper();
        query.lambda().eq(RolePermission::getRoleId, role.getId());
        List<RolePermission> dbRolePermissionList = rolePermissionService.getEntityList(query);

        //组装新的权限信息
        StringBuffer newPermissionBuffer = new StringBuffer("_");
        if(V.notEmpty(role.getPermissionList())){
            role.getPermissionList()
                    .stream()
                    .forEach(permission -> newPermissionBuffer.append(permission.getId()).append("_"));
        }

        //筛选出需要被删除的（角色-权限）
        StringBuffer dbPermissionIdBuffer = new StringBuffer("_");
        if(V.notEmpty(dbRolePermissionList)){
            dbRolePermissionList.stream()
                    .forEach(rolePermission -> {
                        dbPermissionIdBuffer.append(rolePermission.getPermissionId()).append("_");
                        if (!newPermissionBuffer.toString().contains(S.join("_", rolePermission.getPermissionId(), "_"))) {
                            rolePermission.setDeleted(true);
                            createOrDeleteRolePermissionList.add(rolePermission);
                        }
                    });
        }

        //对比数据库 筛选 + 构建 页面提交需要添加的权限信息
        if(V.notEmpty(role.getPermissionList())){
            role.getPermissionList()
                    .stream()
                    .forEach(permission -> {
                        if (!dbPermissionIdBuffer.toString().contains(S.join("_", role.getId(), "_"))) {
                            RolePermission entity = new RolePermission();
                            entity.setRoleId(role.getId());
                            entity.setPermissionId(permission.getId());
                            createOrDeleteRolePermissionList.add(entity);
                        }
                    });
        }
    }
}
