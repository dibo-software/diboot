package com.diboot.shiro.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.shiro.entity.SysUser;
import com.diboot.shiro.exception.ShiroCustomException;
import com.diboot.shiro.mapper.SysUserMapper;
import com.diboot.shiro.service.SysUserService;
import com.diboot.shiro.util.AuthHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 用户相关Service
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
@Service
@Slf4j
public class SysUserServiceImpl extends BaseServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    /**
     * 注册用户
     *
     * @param sysUser
     * @return
     * @throws Exception
     */
    @Override
    public boolean register(SysUser sysUser) throws Exception {

        if (V.isEmpty(sysUser.getUsername()) || V.isEmpty(sysUser.getPassword())) {
            throw new ShiroCustomException(Status.FAIL_INVALID_PARAM, "用户名密码不能为空!");
        }
        LambdaQueryWrapper<SysUser> wrapper = Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getUsername, sysUser.getUsername());
        SysUser dbSysUser = getOne(wrapper);
        //校验数据库中数据是否已经存在
        if (V.notEmpty(dbSysUser)) {
            throw new ShiroCustomException(Status.FAIL_VALIDATION, "用户已经存在！");
        }
        String salt = AuthHelper.createSalt();
        String password = AuthHelper.encryptMD5(sysUser.getPassword(), salt, true);
        sysUser.setSalt(salt);
        sysUser.setDepartmentId(0L);
        sysUser.setPassword(password);
        boolean success = createEntity(sysUser);
        if (success) {
            log.info("【用户注册】<== 成功");
        } else {
            log.info("【用户注册】<== 失败");
        }
        return success;
    }
}
