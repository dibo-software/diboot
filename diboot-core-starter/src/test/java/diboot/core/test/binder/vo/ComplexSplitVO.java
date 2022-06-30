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
import diboot.core.test.binder.entity.Organization;
import diboot.core.test.binder.entity.TestUploadFile;
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
public class ComplexSplitVO extends Organization {

    // ，拆分的id值绑定 （中间表1-n主键绑定）
    @BindEntityList(entity = TestUploadFile.class, condition="this.manager_id=user.id AND user.`character`=uuid",
            splitBy= Cons.SEPARATOR_COMMA)
//    @BindEntityList(entity = TestUploadFile.class, condition="this.manager_id=\"USER\".id AND \"USER\".character=uuid",
//            splitBy= Cons.SEPARATOR_COMMA)
    private List<TestUploadFile> managerPhotos;

    // ，拆分的id值绑定
    @BindFieldList(entity = TestUploadFile.class, field = "fileName",
            condition="this.manager_id=user.id AND user.`character`=uuid", splitBy= Cons.SEPARATOR_COMMA)
//    @BindFieldList(entity = TestUploadFile.class, field = "fileName",
//            condition="this.manager_id=\"USER\".id AND \"USER\".character=uuid", splitBy= Cons.SEPARATOR_COMMA)
    private List<String> managerPhotoNames;

    // 中间表1-n非主键绑定
    @BindEntityList(entity = TestUploadFile.class, condition="this.manager_id=user.id AND user.id=rel_obj_id AND rel_obj_type = 'IamUser'")
    //@BindEntityList(entity = TestUploadFile.class, condition="this.manager_id=\"USER\".id AND \"USER\".id=rel_obj_id AND rel_obj_type = 'IamUser'")
    private List<TestUploadFile> managerPhotoList;

}
