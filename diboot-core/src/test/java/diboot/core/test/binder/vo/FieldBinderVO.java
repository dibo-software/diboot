package diboot.core.test.binder.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.binding.annotation.BindField;
import com.diboot.core.binding.annotation.BindMetadata;
import diboot.core.test.binder.entity.Department;
import diboot.core.test.binder.entity.Organization;
import diboot.core.test.binder.entity.User;

/**
 * <Description>
 *
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/06/22
 */
public class FieldBinderVO extends User{
    private static final long serialVersionUID = 3526115343377985725L;

    // 字段关联，相同条件的entity+condition将合并为一条SQL查询
    @BindField(entity= Department.class, field="name", condition="this.department_id=id AND parent_id IS NOT NULL")
    private String deptName;

    // 支持级联字段关联，相同条件的entity+condition将合并为一条SQL查询
    @BindField(entity = Organization.class, field="name", condition="this.department_id=department.id AND department.org_id=id")
    private String orgName;
    @BindField(entity = Organization.class, field="telphone", condition="this.department_id=department.id AND department.org_id=id")
    private String orgTelphone;

    // 绑定元数据枚举
    @BindMetadata(type="GENDER", field = "gender")
    private String genderLabel;

    public String getDeptName() {
        return deptName;
    }
    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
    public String getOrgName() {
        return orgName;
    }
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
    public String getOrgTelphone() {
        return orgTelphone;
    }
    public void setOrgTelphone(String orgTelphone) {
        this.orgTelphone = orgTelphone;
    }

    public String getGenderLabel() {
        return genderLabel;
    }

    public void setGenderLabel(String genderLabel) {
        this.genderLabel = genderLabel;
    }
}
