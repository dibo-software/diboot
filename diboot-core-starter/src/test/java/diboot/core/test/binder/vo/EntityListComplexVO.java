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

import com.diboot.core.binding.annotation.BindCount;
import com.diboot.core.binding.annotation.BindEntityList;
import com.diboot.core.binding.annotation.BindFieldList;
import diboot.core.test.binder.entity.Role;
import diboot.core.test.binder.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * <Description>
 *
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/06/22
 */
@Getter
@Setter
@Accessors(chain = true)
public class EntityListComplexVO extends User {

    private String userType = "OrgUser";

    // 支持通过中间表的多-多Entity实体关联
    @BindEntityList(entity = Role.class, condition="this.id=user_role.user_id AND user_role.role_id=id AND user_role.user_id>1")
    private List<Role> roleList;

    @BindCount(entity = Role.class, condition="this.id=user_role.user_id AND user_role.role_id=id")
    private Integer roleCount;

    // 支持通过中间表的多-多Entity的单个属性集
    @BindFieldList(entity = Role.class, field = "code", condition="this.id=user_role.user_id AND user_role.role_id=id")
    private List<String> roleCodes;

    // 支持通过中间表的多-多Entity的单个属性集
    @BindFieldList(entity = Role.class, field = "createTime", condition="this.id=user_role.user_id AND user_role.role_id=id")
    private List<Date> roleCreateDates;

}
