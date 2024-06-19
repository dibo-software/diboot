package com.diboot.iam.vo;

import com.diboot.core.binding.annotation.BindFieldList;
import com.diboot.iam.entity.IamResource;
import com.diboot.iam.entity.IamRole;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.List;

/**
 * 前端资源与角色集合的VO
 * @author mazc
 * @version v1.0
 * @date 2020/09/09
 */
@Getter @Setter
public class ResourceRoleVO extends IamResource {
    private static final long serialVersionUID = 7155540339001886956L;

    @BindFieldList(entity = IamRole.class, field = "code", condition = "this.id=dbt_iam_role_resource.resource_id AND dbt_iam_role_resource.role_id=id")
    private List<String> roleCodes;

}
