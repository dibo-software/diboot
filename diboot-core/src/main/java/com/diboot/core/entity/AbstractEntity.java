/*
 * Copyright (c) 2015-2021, www.dibo.ltd (service@dibo.ltd).
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

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

/**
 * Entity抽象父类
 * @author Yangzhao@dibo.ltd
 * @version v2.0
 * Copyright © diboot.com
 */
public abstract class AbstractEntity<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 10202L;

    /**
     * 默认主键字段id，类型为Long型自增，转json时转换为String
     */
    @TableId(type = IdType.AUTO)
    private T id;

    public AbstractEntity setId(T id){
        this.id = id;
        return this;
    }

    public T getId(){
        return this.id;
    }

    /**
     * Entity对象转为String
     * @return
     */
    @Override
    public String toString(){
        return this.getClass().getName()+ ":"+this.getId();
    }
}
