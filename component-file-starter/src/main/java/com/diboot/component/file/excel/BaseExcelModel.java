package com.diboot.component.file.excel;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/***
 * excel数据导入导出实体基类
 * @auther wangyl@dibo.ltd
 * @date 2019-10-9
 */
public class BaseExcelModel implements Serializable {
    private static final long serialVersionUID = 6343247548525494223L;

    /**
     * 验证错误
     */
    @JSONField(serialize = false)
    private String validateError;

    @JSONField(serialize = false)
    private int rowIndex;

    public int getRowIndex(){
        return rowIndex;
    }

    public String getValidateError(){
        return validateError;
    }

    /**
     * 绑定错误
     * @param validateError
     */
    public void addValidateError(String validateError){
        if(this.validateError == null){
            this.validateError = validateError;
        }
        else{
            this.validateError += ", " + validateError;
        }
    }

    public void setRowIndex(int rowIndex){
        this.rowIndex = rowIndex;
    }
}
