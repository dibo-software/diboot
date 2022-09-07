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

import com.diboot.core.binding.annotation.BindEntity;
import com.diboot.core.binding.annotation.BindEntityList;
import com.diboot.core.binding.annotation.BindField;
import com.diboot.core.binding.annotation.BindFieldList;
import com.diboot.core.entity.Dictionary;
import diboot.core.test.binder.entity.Organization;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * <Description>
 *
 * @author mazc
 * @version v2.1.2
 * @date 2020/08/25
 */
@Getter @Setter
public class MulColJoinVO {

    private String dictId;
    private String dictType;

    private Long orgPid;
    private String telphone;

    @BindEntity(entity= Dictionary.class, condition="this.dict_type=type AND this.dict_id=id")
    private Dictionary parentDict;

    @BindField(entity= Dictionary.class, field = "itemName", condition="this.dict_type=type AND this.dict_id=id")
    private String parentDictName;

    @BindEntityList(entity = Organization.class, condition = "this.org_pid=parent_id AND this.telphone=telphone")
    private List<Organization> orgList;

    @BindFieldList(entity = Organization.class, field = "name", condition = "this.org_pid=parent_id AND this.telphone=telphone")
    private List<String> orgNames;

}
