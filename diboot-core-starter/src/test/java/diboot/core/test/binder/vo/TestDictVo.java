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
import com.diboot.core.binding.annotation.BindField;
import com.diboot.core.entity.Dictionary;
import com.diboot.core.vo.DictionaryVO;
import lombok.Getter;
import lombok.Setter;

/**
 * 测试字典VO
 * @author Jerry@dibo.ltd
 * @version v2.1
 * @date 2020/08/19
 */
@Getter @Setter
public class TestDictVo extends DictionaryVO {

    @BindEntity(entity= Dictionary.class, condition="this.type=type AND this.parent_id=id")
    private Dictionary parentDict;

    @BindField(entity= Dictionary.class, field = "itemName", condition="this.type=type AND this.parent_id=id")
    private String parentDictName;

}
