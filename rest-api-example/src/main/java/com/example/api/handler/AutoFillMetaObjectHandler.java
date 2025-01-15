package com.example.api.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.diboot.core.config.Cons;
import com.diboot.iam.util.IamSecurityUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 字段自动填充策略处理类
 *
 * @author MyName
 * @version 1.0
 * @date 2022-12-30
 * Copyright © MyCompany
 */
@Component
public class AutoFillMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, Cons.FieldName.createTime.name(), LocalDateTime::now, LocalDateTime.class);
        this.strictInsertFill(metaObject, Cons.FieldName.createBy.name(), IamSecurityUtils::getCurrentUserId, String.class);
        this.strictInsertFill(metaObject, Cons.FieldName.updateTime.name(), LocalDateTime::now, LocalDateTime.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, Cons.FieldName.updateTime.name(), LocalDateTime::now, LocalDateTime.class);
        this.strictUpdateFill(metaObject, Cons.FieldName.updateBy.name(), IamSecurityUtils::getCurrentUserId, String.class);
    }
}