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
package com.diboot.iam.dto;

import com.diboot.iam.entity.IamUserPosition;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 用户岗位DTO
 * @author mazc@dibo.ltd
 * @version v2.2
 * @date 2020/12/1
 */
@Getter
@Setter
@Accessors(chain = true)
public class IamUserPositionBatchDTO extends IamUserPosition {
    private static final long serialVersionUID = 5531280576807490425L;

    /***
     * 用户岗位关联列表
     */
    private List<IamUserPosition> userPositionList;
}
