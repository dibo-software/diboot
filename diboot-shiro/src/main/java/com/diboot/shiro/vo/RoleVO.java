package com.diboot.shiro.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.binding.annotation.BindDict;
import com.diboot.core.binding.annotation.BindEntityList;
import com.diboot.shiro.entity.Permission;
import com.diboot.shiro.entity.Role;
import lombok.Data;

import java.util.List;

/**
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
@Data
public class RoleVO extends Role {

    private static final long serialVersionUID = 860775286174387052L;

    @BindDict(type="ROLE_STATUS", field="status")
    private String statusLabel;

    @TableField(exist = false)
    private List<Permission> menuList;

    /**
     * 扩展账户id
     */
    private Long userId;

}