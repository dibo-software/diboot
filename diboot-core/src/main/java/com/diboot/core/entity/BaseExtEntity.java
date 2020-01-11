package com.diboot.core.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.util.JSON;
import com.diboot.core.util.V;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 附带extdata扩展字段的Entity父类
 * @author Mazhicheng
 * @version v2.0
 * @date 2018/12/27
 */
public abstract class BaseExtEntity extends BaseEntity {
    private static final long serialVersionUID = 10204L;

    @TableField
    private String extdata; //扩展数据

    @TableField(exist = false)
    private Map<String, Object> extdataMap;

    public String getExtdata() {
        if(V.isEmpty(this.extdataMap)){
            return null;
        }
        return JSON.toJSONString(this.extdataMap);
    }

    public BaseExtEntity setExtdata(String extdata) {
        if(V.notEmpty(extdata)){
            this.extdataMap = JSON.toLinkedHashMap(extdata);
        }
        return this;
    }

    /***
     * 从extdata JSON中提取扩展属性值
     * @param extAttrName
     * @return
     */
    public Object getFromExt(String extAttrName){
        if(this.extdataMap == null){
            return null;
        }
        return this.extdataMap.get(extAttrName);
    }

    /***
     * 添加扩展属性和值到extdata JSON中
     * @param extAttrName
     * @param extAttrValue
     */
    public BaseExtEntity addIntoExt(String extAttrName, Object extAttrValue){
        if(extAttrName == null && extAttrValue == null){
            return this;
        }
        if(this.extdataMap == null){
            this.extdataMap = new LinkedHashMap<>();
        }
        this.extdataMap.put(extAttrName, extAttrValue);
        return this;
    }

}
