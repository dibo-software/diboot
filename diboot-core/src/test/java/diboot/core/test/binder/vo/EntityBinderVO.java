package diboot.core.test.binder.vo;

import com.diboot.core.binding.annotation.BindEntity;
import diboot.core.test.binder.entity.Department;
import diboot.core.test.binder.entity.Organization;
import diboot.core.test.binder.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/1/30
 */
@Getter
@Setter
@Accessors(chain = true)
public class EntityBinderVO extends User {
    private static final long serialVersionUID = 3526115343377985725L;

    // 字段关联，相同条件的entity+condition将合并为一条SQL查询
    @BindEntity(entity= Department.class, condition="this.department_id=id AND parent_id >= 0")
    private Department department;

    // 通过中间表关联Entity
    @BindEntity(entity = Organization.class, condition = "this.department_id=department.id AND department.org_id=id AND parent_id=0") // AND ...
    private OrganizationVO organizationVO;

}