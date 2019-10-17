package com.diboot.shiro.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.diboot.core.service.BaseService;
import com.diboot.core.vo.Pagination;
import com.diboot.shiro.entity.Permission;
import com.diboot.shiro.entity.Role;
import com.diboot.shiro.enums.IUserType;
import com.diboot.shiro.vo.RoleVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 角色相关Service
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
public interface RoleService extends BaseService<Role> {

    /***
     * 获取角色列表
     * @param queryWrapper
     * @param pagination
     * @return
     */
    List<RoleVO> getRoleList(Wrapper queryWrapper, Pagination pagination);

    /***
     * 获取角色信息
     * @param id
     * @return
     */
    RoleVO getRole(Long id);

    /***
     * 新建角色信息
     * @param role
     * @return
     */
    boolean createRole(Role role);

    /***
     * 显示更新页面
     * @param id
     * @return
     */
    RoleVO toUpdatePage(Long id);

    /***
     * 修改角色信息
     * @param role
     * @return
     */
    boolean updateRole(Role role);

    /***
     * 删除角色信息
     * @param id
     * @return
     */
    boolean deleteRole(Long id);

    /***
     * 获取所有菜单,以及菜单下的资源
     * @return
     */
    List<Permission> getAllMenu();

    /***
     * 根据用户类型和用户id获取角色关联权限列表
     * @param userType
     * @param userId
     * @return
     */
    List<RoleVO> getRelatedRoleAndPermissionListByUser(String userType, Long userId);

    /**
     * 根据用户id获取所有角色
     * @param userIdList
     * @return
     */
    List<RoleVO> getRoleByUserIdList(List<Long> userIdList);

}
