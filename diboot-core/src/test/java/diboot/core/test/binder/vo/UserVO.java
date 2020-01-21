package diboot.core.test.binder.vo;

import com.diboot.core.binding.annotation.BindField;
import diboot.core.test.binder.entity.Department;
import diboot.core.test.binder.entity.User;

/**
 * <Description>
 *
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/06/22
 */
public class UserVO extends User{
    private static final long serialVersionUID = 3526115343377985709L;

    // 字段关联，附加更多条件
    @BindField(entity= Department.class, field="name", condition="this.department_id=id AND parent_id IS NOT NULL AND name = '研发组'")
    private String deptName;

    public String getDeptName() {
        return deptName;
    }
    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
}
