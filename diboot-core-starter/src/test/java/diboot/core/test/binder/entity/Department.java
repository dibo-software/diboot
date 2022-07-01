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
import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.diboot.core.binding.query.Strategy;
import com.diboot.core.data.access.DataAccessCheckpoint;
import com.diboot.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Department
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2018/12/27
 */
@Getter
@Setter
@Accessors(chain = true)
public class Department extends BaseEntity {
    private static final long serialVersionUID = -4849732665419794547L;

    @BindQuery(comparison = Comparison.EQ, strategy = Strategy.INCLUDE_NULL)
    @TableField
    private Long parentId;

    @TableField
    @DataAccessCheckpoint()
    private Long orgId;

    @BindQuery(comparison = Comparison.STARTSWITH)
    @TableField
    private String name;

    @TableField("`character`")
    private String character;
}
