package diboot.core.test.binder.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.entity.BaseEntity;

/**
 * 定时任务
 * @author Mazhicheng
 * @version v2.0
 * @date 2018/12/27
 */
public class Department extends BaseEntity {
    private static final long serialVersionUID = -4849732665419794547L;

    @TableField
    private Long parentId;

    @TableField
    private Long orgId;

    @TableField
    private String name;

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}