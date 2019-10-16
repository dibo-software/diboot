package com.diboot.shiro.service;

import com.diboot.core.service.BaseService;
import com.diboot.shiro.entity.SysUser;
import com.diboot.shiro.enums.IUserType;
import com.diboot.shiro.vo.SysUserVO;

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
     * @param id
     * @param iUserType
     * @return
     * @throws Exception
     */
    boolean deleteSysUser(Long id, IUserType iUserType) throws Exception;

}
