package diboot.core.test.binder.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author JerryMa
 * @version v2.2.1
 * @date 2021/4/29
 * Copyright © diboot.com
 */
@Getter @Setter @Accessors(chain = true)
public abstract class MyBaseEntity implements Serializable {
    private static final long serialVersionUID = 3766706110662091336L;

    @TableLogic
    @JsonIgnore
    @TableField(value = "is_del", select = false)
    private boolean deleted = false;

    @TableField(
            insertStrategy = FieldStrategy.NEVER,
            updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime createTs;

    @TableField(
            insertStrategy = FieldStrategy.NEVER,
            updateStrategy = FieldStrategy.NOT_NULL)
    private LocalDateTime updateTs;

}
