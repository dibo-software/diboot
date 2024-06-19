/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.diboot.core.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.diboot.core.config.Cons;
import com.diboot.core.util.JSON;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Entity基础父类
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2018/12/27
 */
@Getter
@Setter
@Accessors(chain = true)
public abstract class BaseEntity<T extends Serializable> extends AbstractEntity<T> {
    private static final long serialVersionUID = 10203L;

    /**
     * 默认逻辑删除标记，is_deleted=0有效
     */
    @TableLogic
    @JsonIgnore
    @TableField(value = Cons.COLUMN_IS_DELETED, select = false)
    private boolean deleted = false;

    /**
     * 默认记录创建时间字段，新建时由数据库赋值
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /***
     * Entity对象转为map
     * @return
     */
    public Map<String, Object> toMap(){
        String jsonStr = JSON.stringify(this);
        return JSON.toMap(jsonStr);
    }

}