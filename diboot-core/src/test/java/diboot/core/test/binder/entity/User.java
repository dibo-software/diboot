package diboot.core.test.binder.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.entity.BaseEntity;
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
public class User extends BaseEntity {
    private static final long serialVersionUID = 3050761344045195972L;

    @TableField
    private Long departmentId;

    @TableField
    private String username;

    @TableField
    private String gender;

}
