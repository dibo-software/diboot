package com.diboot.shiro.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.diboot.core.service.BaseService;
import com.diboot.core.vo.Pagination;
import com.diboot.shiro.entity.Permission;
import com.diboot.shiro.entity.Role;
import com.diboot.shiro.vo.RoleVO;

import java.util.List;

/**
 * 角色相关Service
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
public interface RoleService extends BaseService<Role> {

    //获取角色列表
    List<RoleVO> getRoleList(Wrapper queryWrapper, Pagination pagination);

    //获取角色信息
    RoleVO getRole(Long id);

    //新建角色信息
    boolean createRole(Role role);

    //显示更新页面
    RoleVO toUpdatePage(Long id);

    //修改角色信息
    boolean updateRole(Role role);

    //删除角色信息
    boolean deleteRole(Long id);

    //获取所有菜单
    List<Permission> getAllMenu();

    /***
     * 根据用户类型和用户id获取角色关联权限列表
     * @param userType
     * @param userId
     * @return
     */
    List<RoleVO> getRelatedRoleAndPermissionListByUser(String userType, Long userId);

}
