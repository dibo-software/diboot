package com.diboot.shiro.service;

import com.diboot.core.service.BaseService;
import com.diboot.shiro.entity.SysUser;
import com.diboot.shiro.entity.TokenAccountInfo;
import com.diboot.shiro.enums.IUserType;
import com.diboot.shiro.vo.SysUserVO;

import java.util.List;
import java.util.Map;

/**
 * 用户相关Service
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
public interface SysUserService extends BaseService<SysUser> {

    /***
     * 查看
     * @param id
     * @return
     * @throws Exception
     */
    SysUserVO getSysUser(Long id) throws Exception;

    /**
     * 创建
     * @param sysUser
     * @param iUserType 用户类型
     * @return
     * @throws Exception
     */
    boolean createSysUser(SysUser sysUser, IUserType iUserType) throws Exception;

    /***
     * 更新
     * @param user
     * @param iUserType
     * @return
     * @throws Exception
     */
    boolean updateSysUser(SysUser user, IUserType iUserType) throws Exception;

    /***
     * 删除
     * @param userId  用户id
     * @param iUserType 用户类型
     * @return
     * @throws Exception
     */
    boolean deleteSysUser(Long userId, IUserType iUserType) throws Exception;

    /**
     * 获取已经登录的账号信息（sysUser-绑定角色 + 权限）
     * @param account
     * @return
     * @throws Exception
     */
    SysUser getLoginAccountInfo(TokenAccountInfo account) throws Exception;

    /**
     * 根据用户信息的id  和  用户类型，获取对应的账户 和 账户关联的信息
     * @param userIdList
     * @param iUserType
     * @return 用户id - 账户
     */
    Map<Long, SysUser> getSysUserListWithRolesAndPermissionsByUserIdList(List<Long> userIdList, IUserType iUserType);

    /**
     * 根据用户信息获取 和 用户类型，获取对应的账户
     * @param userId
     * @param iUserType
     * @return
     */
    SysUser getSysUser(Long userId, IUserType iUserType);

}
