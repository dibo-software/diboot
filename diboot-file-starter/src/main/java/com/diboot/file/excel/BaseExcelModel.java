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
package com.diboot.file.excel;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.*;

/***
 * excel数据导入导出实体基类
 * @auther wangyl@dibo.ltd
 * @date 2019-10-9
 */
public class BaseExcelModel implements Serializable {
    private static final long serialVersionUID = 6343247548525494223L;

    @JsonIgnore
    @ExcelIgnore
    private int rowIndex;

    /**
     * 验证错误
     */
    @Deprecated
    @JsonIgnore
    @ExcelIgnore
    private String validateError;

    /**
     * 批注
     */
    @JsonIgnore
    @ExcelIgnore
    private Map<String, List<String>> comment;

    public int getRowIndex(){
        return rowIndex;
    }

    @Deprecated
    public String getValidateError(){
        return validateError;
    }

    public Map<String, List<String>> getComment() {
        return comment == null ? Collections.emptyMap() : comment;
    }

    public void setRowIndex(int rowIndex){
        this.rowIndex = rowIndex;
    }

    /**
     * 绑定错误
     * @param validateError
     */
    @Deprecated
    public void addValidateError(String validateError){
        if(this.validateError == null){
            this.validateError = validateError;
        }
        else{
            this.validateError += ", " + validateError;
        }
    }

    /**
     * 添加批注
     *
     * @param propertyName 属性
     * @param message      信息
     */
    public void addComment(String propertyName, String message) {
        if (comment == null) {
            comment = new HashMap<>();
        }
        comment.computeIfAbsent(propertyName, key -> new ArrayList<>()).add(message);
    }

}
