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
package diboot.core.test.binder.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.diboot.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 地区，测试uuid
 * @author wangyl@dibo.ltd
 * @version v2.0
 * @date 2021/08/27
 */
@Getter @Setter @Accessors(chain = true)
public class TestRegion extends BaseEntity {
    private static final long serialVersionUID = -1391001660726027259L;

    // 废弃默认主键
    @TableField(exist = false)
    private Long id;
    // 声明新主键uuid
    @TableId(type = IdType.ASSIGN_UUID)
    private String uuid;

    /**
     * 名称
     */
    private String name;

    /**
     * 层级
     */
    private Integer level;

    /**
     * 名称
     */
    private String code;

    /**
     * 父级id
     */
    private String parentId;

    /**
     * 子节点
     */
    private List<TestRegion> children;
}