package com.diboot.shiro.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.entity.BaseEntity;
import com.diboot.core.util.V;
import com.diboot.shiro.config.AuthType;
import com.diboot.shiro.entity.SysUser;
import com.diboot.shiro.enums.IUserType;
import com.diboot.shiro.jwt.BaseJwtAuthenticationToken;
import com.diboot.shiro.service.AuthWayService;
import com.diboot.shiro.service.SysUserService;
import com.diboot.shiro.util.AuthHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/***
 * 用户名密码认证实现
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
@Service
public class UsernamePasswordAuthWayServiceImpl implements AuthWayService {

    private final Logger logger = LoggerFactory.getLogger(UsernamePasswordAuthWayServiceImpl.class);

    @Autowired
    private SysUserService sysUserService;

    private AuthType authType = AuthType.USERNAME_PASSWORD;

    private BaseJwtAuthenticationToken token;

    @Override
    public AuthType authType() {
        return authType;
    }

    @Override
    public void initByToken(BaseJwtAuthenticationToken token) {
        this.token = token;
    }

    @Override
    public BaseEntity getUser() {
        logger.debug("【获取用户】==>当前 登陆用户：{}-{}", token.getAccount(), token.getIUserType().getType());
        LambdaQueryWrapper<SysUser> query = Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getUsername, token.getAccount())
                .eq(SysUser::getUserType, token.getIUserType().getType());
        List<SysUser> userList = sysUserService.getEntityList(query);
        if (V.isEmpty(userList)){
            return null;
        }
        return userList.get(0);
    }

    @Override
    public boolean requirePassword() {
        return authType.isRequirePassword();
    }

    @Override
    public boolean isPasswordMatch() {
        String password = token.getPassword();

        // 构建查询条件
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(SysUser::getUsername, token.getAccount());

        // 获取单条用户记录
        List<SysUser> userList = sysUserService.getEntityList(queryWrapper);
        if (V.isEmpty(userList)) {
            return false;
        }
        SysUser sysUser = userList.get(0);
        //加密后比较
        if (!V.equals(AuthHelper.encryptMD5(password, sysUser.getSalt(), true), sysUser.getPassword())) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isPreliminaryVerified() {
        return false;
    }

    @Override
    public Long getExpiresInMinutes() {
        return null;
    }
}
