package diboot.core.test.binder.vo;

import com.baomidou.mybatisplus.annotation.TableField;

/**
 * 定时任务
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2018/12/27
 */
public class DepartmentVO {
    private static final long serialVersionUID = -4849732665419794547L;

    @TableField
    private Long parentId;

    @TableField(exist = false)
    private String name;

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
}