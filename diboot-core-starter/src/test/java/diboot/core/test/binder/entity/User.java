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
package diboot.core.test.binder.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diboot.core.data.copy.Accept;
import com.diboot.core.entity.BaseEntity;
import com.diboot.core.util.D;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/1/30
 */
@Getter
@Setter
@Accessors(chain = true)
//@TableName("\"USER\"")
public class User extends BaseEntity {
    private static final long serialVersionUID = 3050761344045195972L;

    @TableField
    private Long departmentId;

    @TableField
    private String username;

    @Accept(name = "itemName")
    @TableField
    private String gender;

    //@JsonFormat(pattern = D.FORMAT_DATE_Y4MD)
    private Date birthdate;

    @JsonFormat(pattern = D.FORMAT_DATE_Y4MD)
    private LocalDateTime localDatetime;

    @TableField("`character`")
    private String character;
}
