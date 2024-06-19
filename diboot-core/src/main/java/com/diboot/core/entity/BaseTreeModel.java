package com.diboot.core.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

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
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;
}