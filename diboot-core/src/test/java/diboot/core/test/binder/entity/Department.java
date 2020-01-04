package diboot.core.test.binder.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 定时任务
 * @author Mazhicheng
 * @version v2.0
 * @date 2018/12/27
 */
@Getter
@Setter
@Accessors(chain = true)
public class Department extends BaseEntity {
    private static final long serialVersionUID = -4849732665419794547L;

    @TableField
    private Long parentId;

    @TableField
    private Long orgId;

    @TableField
    private String name;
}