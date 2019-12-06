package diboot.core.test.binder.vo;

import com.baomidou.mybatisplus.annotation.TableField;

/**
 * <Description>
 *
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/12/06
 */
public class OrganizationVO {

    private Long parentId;

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
