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
package diboot.core.test.binder.dto;

import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.diboot.core.binding.query.Strategy;
import diboot.core.test.binder.entity.Department;
import diboot.core.test.binder.entity.Organization;
import diboot.core.test.binder.entity.Role;
import diboot.core.test.binder.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * User DTO
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2018/12/27
 */
@Getter
@Setter
public class UserDTO extends User {

    // 字段关联
    @BindQuery(entity= Department.class, field = "name", condition="this.department_id=id") // AND parent_id >= 0
    private String deptName;

    // 字段关联
    @BindQuery(entity= Department.class, field = "id", condition="this.department_id=id") // AND parent_id >= 0
    private Long deptId;

    // 通过中间表关联Entity
    @BindQuery(comparison = Comparison.ENDSWITH, entity = Organization.class, field = "name",
            condition = "this.department_id=department.id AND department.org_id=id AND parent_id=0", strategy = Strategy.INCLUDE_EMPTY)
    private String orgName;
    // LEFT JOIN department r2m ON self.department_id = r2m.id
    // LEFT JOIN organization r1 ON r2m.org_id=r2.id

    @BindQuery(comparison = Comparison.IN, entity = Role.class, field = "code", condition = "this.id=user_role.user_id AND user_role.role_id=id")
    private List<String> roleCodes;
    //private String[] roleCodes;
    // LEFT JOIN user_role r3m ON self.id = r3m.user_id
    // LEFT JOIN role r3 ON r3m.role_id = r3.id

    @BindQuery(entity = Role.class, field = "id", condition = "this.id=user_role.user_id AND user_role.role_id=id")
    private Long roleId;

}