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

import com.diboot.core.binding.annotation.BindCount;
import com.diboot.core.binding.annotation.BindFieldList;
import diboot.core.test.binder.entity.Department;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/1/5
 */
@Getter
@Setter
@Accessors(chain = true)
public class CountSimpleVO extends Department {
    private static final long serialVersionUID = -362116388664907924L;

    // 直接关联多个Entity
    @BindCount(entity = Department.class, condition = "this.id=parent_id")
    private Long childrenCount;

    // 1-n 关联，取单个属性
    @BindFieldList(entity = Department.class, field = "id", condition = "this.id=parent_id")
    private List<Long> childrenIds;

}