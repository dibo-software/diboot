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

import com.diboot.core.binding.annotation.BindDict;
import com.diboot.core.binding.annotation.BindField;
import diboot.core.test.binder.entity.Department;
import diboot.core.test.binder.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

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
public class UserVO extends User {
    private static final long serialVersionUID = 3526115343377985709L;

    // 字段关联，附加更多条件
    @BindField(entity= Department.class, field="name", condition="this.department_id=id AND parent_id IS NOT NULL AND name = '研发组'")
    private String deptName;

    @BindDict(type = "GENDER", field = "gender")
    private String genderLabel;

    @BindField(entity= Department.class, field="name", condition="this.department_id=id")
    private String deptName2;

}
