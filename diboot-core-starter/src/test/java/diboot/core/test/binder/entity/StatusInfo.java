package diboot.core.test.binder.entity;

import com.diboot.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 问题状态实体
 */
@Getter @Setter
public class StatusInfo extends BaseEntity<String> {
    private static final long serialVersionUID = 2901095453152116088L;

    private String userId;

    private String problemId;

    private String remark;

}
