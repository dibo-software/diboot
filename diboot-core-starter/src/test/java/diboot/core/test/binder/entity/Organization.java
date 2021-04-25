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
package diboot.core.test.binder.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 单位Entity
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/1/5
 */
@Getter
@Setter
@Accessors(chain = true)
public class Organization extends BaseEntity {
    private static final long serialVersionUID = -5889309041570465909L;

    @TableField
    private Long parentId;

    @TableField
    private String name;

    @TableField
    private String telphone;

    @TableField
    private Long managerId;

}
