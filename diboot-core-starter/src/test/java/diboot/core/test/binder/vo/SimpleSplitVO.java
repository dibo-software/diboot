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
package diboot.core.test.binder.vo;

import com.diboot.core.binding.annotation.BindEntityList;
import com.diboot.core.binding.annotation.BindFieldList;
import com.diboot.core.config.Cons;
import diboot.core.test.binder.entity.Department;
import diboot.core.test.binder.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <Description>
 *
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2021/08/27
 */
@Getter
@Setter
@Accessors(chain = true)
public class SimpleSplitVO extends Department {

    // ，拆分的id值绑定
    @BindEntityList(entity = User.class, condition="this.`character`=id", splitBy= Cons.SEPARATOR_COMMA)
    //@BindEntityList(entity = User.class, condition="this.character=id", splitBy= Cons.SEPARATOR_COMMA)
    private List<User> managers;


    // ，拆分的id值绑定
    @BindFieldList(entity = User.class, field = "username", condition="this.`character`=id", splitBy= Cons.SEPARATOR_COMMA)
    //@BindFieldList(entity = User.class, field = "username", condition="this.character=id", splitBy= Cons.SEPARATOR_COMMA)
    private List<String> managerNames;

}
