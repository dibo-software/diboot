package diboot.core.test.binder.vo;

import com.diboot.core.binding.annotation.BindEntityList;
import diboot.core.test.binder.entity.Role;
import diboot.core.test.binder.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <Description>
 *
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/06/22
 */
@Getter
@Setter
@Accessors(chain = true)
public class EntityListComplexBinderVO extends User {

    private String userType = "OrgUser";

    // 支持通过中间表的多-多Entity实体关联
    @BindEntityList(entity = Role.class, condition="this.id=user_role.user_id AND user_role.role_id=id")
    private List<Role> roleList;

}
