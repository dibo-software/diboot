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
package diboot.core.test.binder.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.binding.annotation.BindEntity;
import diboot.core.test.binder.entity.Department;
import diboot.core.test.binder.entity.Organization;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Department VO
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2018/12/27
 */
@Getter
@Setter
@Accessors(chain = true)
public class DepartmentVO {
    private static final long serialVersionUID = -4849732665419794547L;

    @TableField
    private Long parentId;

    @TableField(exist = false)
    private String name;

    @TableField
    private Long orgId;

    // 关联Entity
    @BindEntity(entity = Department.class, condition = "this.parent_id=id") // AND ...
    private Department department;

    // 关联Entity，赋值给VO
    @BindEntity(entity = Organization.class, condition = "this.org_id=id") // AND ...
    private OrganizationVO organizationVO;

}