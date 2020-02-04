package com.diboot.iam.dto;

import com.diboot.iam.entity.IamRole;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 角色表单接收类
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/18
 */
@Getter
@Setter
@Accessors(chain = true)
public class IamRoleFormDTO extends IamRole {

    private static final long serialVersionUID = 1444823850258901617L;

    /***
     * 权限id列表
     */
    private List<Long> permissionIdList;
}
