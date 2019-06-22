package diboot.core.test.binder.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.entity.BaseEntity;

/**
 * 单位Entity
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/1/5
 */
public class Organization extends BaseEntity {
    private static final long serialVersionUID = -5889309041570465909L;

    @TableField
    private Long parentId;

    @TableField
    private String name;

    @TableField
    private String telphone;

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelphone() {
        return telphone;
    }

    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }
}
