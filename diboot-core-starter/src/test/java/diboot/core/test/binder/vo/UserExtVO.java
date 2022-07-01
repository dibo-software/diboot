/*
 * Copyright (c) 2015-2029, www.dibo.ltd (service@dibo.ltd).
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

import com.diboot.core.binding.annotation.BindField;
import com.diboot.core.binding.annotation.BindFieldList;
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
public class UserExtVO extends User {

    // 字段关联+主从表附属条件
    @BindField(entity= Department.class, field = "name", condition="this.department_id=id AND this.gender='F' AND parent_id > 0")
    private String deptName;

    // 字段关联+主从表附属条件
    @BindField(entity= Department.class, field = "name", condition="this.department_id=id AND this.gender like 'F%' AND parent_id > 0")
    private String deptNameLike;

    // 字段关联+主从表附属条件
    @BindField(entity= Department.class, field = "name", condition="this.department_id=id AND this.gender IN('F', 'U') AND parent_id > 0")
    private String deptNameIn;

    // 字段关联
    @BindField(entity= Department.class, field = "id", condition="this.department_id=id AND parent_id >= 0 AND this.department_id=10002") //
    private Long deptId;

    @BindFieldList(entity = Role.class, field = "code", condition = "this.id=user_role.user_id AND user_role.role_id=id AND this.department_id IN (10002,10001)")
    private List<String> roleCodes;

}