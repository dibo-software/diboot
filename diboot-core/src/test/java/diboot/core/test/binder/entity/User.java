package diboot.core.test.binder.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.entity.BaseEntity;

/**
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/1/30
 */
public class User extends BaseEntity {
    private static final long serialVersionUID = 3050761344045195972L;

    @TableField
    private Long departmentId;

    @TableField
    private String username;

    @TableField
    private String gender;

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
