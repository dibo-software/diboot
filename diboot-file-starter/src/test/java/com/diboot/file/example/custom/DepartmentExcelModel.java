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
package com.diboot.file.example.custom;

import com.alibaba.excel.annotation.ExcelProperty;
import com.diboot.file.excel.BaseExcelModel;
import com.diboot.file.excel.annotation.DuplicateStrategy;
import com.diboot.file.excel.annotation.EmptyStrategy;
import com.diboot.file.excel.annotation.ExcelBindDict;
import com.diboot.file.excel.annotation.ExcelBindField;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;

/**
 * excel列映射文件，支持validation校验
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2020/02/19
 */
@Getter @Setter
public class DepartmentExcelModel extends BaseExcelModel {

    @ExcelBindField(entity = Department.class, field = "name", setIdField = "parentId",
            duplicate = DuplicateStrategy.WARN, empty = EmptyStrategy.SET_0)
    //@NotNull(message = "上级部门不能为空")
    @ExcelProperty(value = "上级部门", index = 0)
    private String parentName;

    private Long parentId;

    @NotNull(message = "必须指定单位")
    @ExcelProperty(value = "单位", index = 1)
    private String orgName;

    @NotNull(message = "必须指定名称")
    @Length(min = 1, max = 10, message = "名称长度不能超过10")
    @ExcelProperty(value = "名称", index = 2)
    private String name;

    @ExcelProperty(value = "数量", index = 3)
    private Integer memCount;

    @NotNull(message = "必须指定status")
    @ExcelBindDict(type = "USER_STATUS")
    @ExcelProperty(value = "状态", index = 4)
    private String userStatus;
}
