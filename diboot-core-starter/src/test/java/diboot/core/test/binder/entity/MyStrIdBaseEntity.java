package diboot.core.test.binder.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.diboot.core.entity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
* 自定义String类型id的BaseEntity
*/
@Getter @Setter @Accessors(chain = true)
public abstract class MyStrIdBaseEntity extends AbstractEntity<String> {

    @TableId(value = "ID_", type = IdType.ASSIGN_UUID)
    private String id;

}
