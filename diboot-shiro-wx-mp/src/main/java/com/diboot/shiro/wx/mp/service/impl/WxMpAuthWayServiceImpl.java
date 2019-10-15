package com.diboot.shiro.wx.mp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.entity.BaseEntity;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.shiro.config.AuthType;
import com.diboot.shiro.entity.SysUser;
import com.diboot.shiro.enums.IUserType;
import com.diboot.shiro.exception.ShiroCustomException;
import com.diboot.shiro.jwt.BaseJwtAuthenticationToken;
import com.diboot.shiro.service.AuthWayService;
import com.diboot.shiro.service.SysUserService;
import com.diboot.shiro.wx.mp.entity.WxMpMember;
import com.diboot.shiro.wx.mp.service.WxMpMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/***
 * 微信公众号认证实现
 * @author Wangyl
 * @version v2.0
 * @date 2019/6/10
 */
@Service
public class WxMpAuthWayServiceImpl implements AuthWayService {

    @Autowired
    private WxMpMemberService wxMpMemberService;

    @Autowired
    private SysUserService sysUserService;

    private AuthType authType = AuthType.WX_MP;

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
        LambdaQueryWrapper<WxMpMember> query = Wrappers.<WxMpMember>lambdaQuery()
                .eq(WxMpMember::getOpenid, token.getAccount());
        List<WxMpMember> wxMpMemberList = wxMpMemberService.getEntityList(query);
        if (V.isEmpty(wxMpMemberList)){
            return null;
        }
        WxMpMember wxMpMember = wxMpMemberList.get(0);
        //绑定账户
        if (V.notEmpty(wxMpMember.getSysUserId())) {
            SysUser sysUser = sysUserService.getEntity(wxMpMember.getSysUserId());
            if (V.isEmpty(sysUser)) {
                throw new ShiroCustomException(Status.FAIL_NO_PERMISSION, "绑定用户后登陆");
            }
        }
        return wxMpMember;
    }

    @Override
    public boolean requirePassword() {
        return authType.isRequirePassword();
    }

    @Override
    public boolean isPasswordMatch() {
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
