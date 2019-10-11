package com.diboot.example.service;

import com.diboot.core.service.BaseService;
import com.diboot.example.entity.SysUser;
import com.diboot.example.vo.SysUserVO;

/**
 * 用户相关Service
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
public interface SysUserService extends BaseService<SysUser> {

    /***
     * 获取详细
     * @param id
     * @return
     */
    SysUserVO getSysUser(Long id);

    /***
     * 新建
     * @param user
     * @return
     */
    boolean createSysUser(SysUser user);

    /***
     * 更新
     * @param user
     * @return
     */
    boolean updateSysUser(SysUser user);

    /***
     * 删除
     * @param id
     * @return
     */
    boolean deleteSysUser(Long id);
}
