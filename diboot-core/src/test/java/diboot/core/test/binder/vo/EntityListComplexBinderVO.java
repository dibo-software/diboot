package diboot.core.test.binder.vo;

import com.diboot.core.binding.annotation.BindEntityList;
import diboot.core.test.binder.entity.Role;
import diboot.core.test.binder.entity.User;

import java.util.List;

/**
 * <Description>
 *
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/06/22
 */
public class EntityListComplexBinderVO extends User {

    private String userType = "OrgUser";

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    // 支持通过中间表的多-多Entity实体关联
    @BindEntityList(entity = Role.class, condition="this.id=user_role.user_id AND user_role.role_id=id") // AND user_role.user_type=this.user_type AND user_role.is_deleted=0
    private List<Role> roleList;

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }
}
