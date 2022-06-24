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

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.diboot.core.util.D;
import diboot.core.test.binder.entity.Department;
import diboot.core.test.binder.entity.Organization;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Department DTO
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2018/12/27
 */
@Getter
@Setter
@Accessors(chain = true)
public class DepartmentDTO implements Serializable {
    private static final long serialVersionUID = 8670003133709715087L;

    private Long parentId;

    @BindQuery(comparison = Comparison.CONTAINS)
    private String name;

    // 绑定join查询
    @BindQuery(comparison = Comparison.STARTSWITH, entity = Organization.class, field = "name", condition = "this.org_id=id")
    private String orgName;

    // 绑定join查询
    @BindQuery(entity = Department.class, field = "name", condition = "this.parent_id=id")
    private String parentName;

    // 多绑定or连接
    @BindQuery(comparison = Comparison.CONTAINS, field = "name")
    @BindQuery(comparison = Comparison.STARTSWITH, field = "`character`")
    //@BindQuery(comparison = Comparison.STARTSWITH, field = "character")
    @BindQuery(comparison = Comparison.ENDSWITH, entity = Organization.class, field = "name", condition = "this.org_id=id")
    private String search;

    // 查询单个日期
    @BindQuery(comparison = Comparison.GE, field = "createTime")
    private Date createTime;

    @BindQuery(comparison = Comparison.LT, field = "createTime")
    private Date createTimeEnd;

    @BindQuery(field = "parent_id", comparison = Comparison.IN)
    private List<Long> parentIds;

    @BindQuery(comparison = Comparison.LIKE)
    @TableField("`character`")
    private String character;

    public Date getCreateTimeEnd(){
        return D.nextDay(createTime);
    }

}
