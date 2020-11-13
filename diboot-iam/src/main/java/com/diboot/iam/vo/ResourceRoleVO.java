package com.diboot.iam.vo;

import com.diboot.core.binding.annotation.BindFieldList;
import com.diboot.iam.entity.IamFrontendPermission;
import com.diboot.iam.entity.IamRole;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 前端资源与角色集合的VO
 * @author mazc
 * @version v1.0
 * @date 2020/09/09
 */
@Getter @Setter
public class ResourceRoleVO extends IamFrontendPermission {

    @BindFieldList(entity = IamRole.class, field = "code", condition = "this.id=iam_role_permission.permission_id AND iam_role_permission.role_id=id")
    private List<String> roleCodes;

}
