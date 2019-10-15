package com.diboot.example.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.binding.annotation.BindDict;
import com.diboot.core.binding.annotation.BindEntityList;
import com.diboot.example.entity.SysUser;
import com.diboot.shiro.entity.Role;
import lombok.Data;

import java.util.List;

/**
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
@Data
public class SysUserVO extends SysUser {

    private static final long serialVersionUID = 5921846275434221060L;

    @BindDict(type="GENDER", field="gender")
    private String genderLabel;

    @BindDict(type="USER_STATUS", field="status")
    private String statusLabel;

    @BindEntityList(entity = Role.class, condition="this.id=user_role.user_id AND user_role.role_id=id AND user_role.user_type='SysUser' AND user_role.is_deleted = 0")
    private List<Role> roleList;

    @TableField(exist = false)
    private List<Long> roleIdList;

}