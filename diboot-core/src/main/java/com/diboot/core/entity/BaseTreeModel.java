package com.diboot.core.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.util.D;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 动态实体模型
 * @author mazc@dibo.ltd
 * @version v3.0
 * @date 2023/5/25
 */
@Getter
@Setter
@Accessors(chain = true)
public class BaseTreeModel extends BaseTreeEntity<String> {
    private static final long serialVersionUID = 10206L;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @TableField()
    private String createBy;

    /**
     * 更新人
     */
    @TableField()
    private String updateBy;
}