package com.diboot.example.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.diboot.core.service.BaseService;
import com.diboot.core.vo.Pagination;
import com.diboot.example.entity.SysUser;
import com.diboot.example.vo.SysUserVO;

import java.util.List;

/**
 * 用户相关Service
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
public interface SysUserService extends BaseService<SysUser> {

    //获取详细
    SysUserVO getSysUser(Long id);

    //新增
    boolean createSysUser(SysUser user);

    //更新
    boolean updateSysUser(SysUser user);

    //删除
    boolean deleteSysUser(Long id);
}
