package com.diboot.example.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.diboot.core.entity.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/1/30
 */
@Data
public class Role extends BaseEntity {
    private static final long serialVersionUID = 3701095453152116088L;

    private String name;

    private String code;

    @JSONField(serialize = false)
    public Date createTime;
}
