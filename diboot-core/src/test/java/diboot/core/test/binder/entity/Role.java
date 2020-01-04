package diboot.core.test.binder.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.diboot.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/1/30
 */
@Getter
@Setter
@Accessors(chain = true)
public class Role extends BaseEntity {
    private static final long serialVersionUID = 3701095453152116088L;

    private String name;

    private String code;

    @JSONField(serialize = false)
    public Date createTime;

}
