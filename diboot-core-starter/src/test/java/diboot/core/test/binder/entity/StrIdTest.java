package diboot.core.test.binder.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

/**
*  String类型id的Entity定义
*/
@Getter @Setter @Accessors(chain = true)
public class StrIdTest extends MyStrIdBaseEntity {

    /**
    * 类型 
    */
    @Length(max=255, message="类型长度应小于255")
    @TableField(value = "TYPE_")
    private String type;

    /**
    * 用户ID 
    */
    @Length(max=255, message="用户ID长度应小于255")
    @TableField(value = "USER_ID_")
    private String userId;

} 
