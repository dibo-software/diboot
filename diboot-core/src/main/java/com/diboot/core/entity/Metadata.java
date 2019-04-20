package com.diboot.core.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 元数据实体
 * @author Mazhicheng
 * @version v2.0
 * @date 2018/12/27
 */
public class Metadata extends BaseExtEntity {
    private static final long serialVersionUID = 11301L;

    /***
     * 上级ID
     */
    @NotNull(message = "上级ID不能为空，如无请设为0")
    @TableField
    private Long parentId = 0L;

    /***
     * 元数据类型
     */
    @NotNull(message = "元数据类型不能为空！")
    @Length(max = 50, message = "元数据类型长度超长！")
    @TableField
    private String type;

    /***
     * 元数据项的显示名称
     */
    @NotNull(message = "元数据项名称不能为空！")
    @Length(max = 100, message = "元数据项名称长度超长！")
    @TableField
    private String itemName;

    /***
     * 元数据项的存储值（编码）
     */
    @Length(max = 100, message = "元数据项编码长度超长！")
    @TableField
    private String itemValue;

    /***
     * 备注信息
     */
    @Length(max = 200, message = "元数据备注长度超长！")
    @TableField
    private String comment;

    /***
     * 排序号
     */
    @TableField
    private int sortId = 99;

    /***
     * 是否为系统预置（预置不可删除）
     */
    @TableField
    private boolean system = true;

    /***
     * 是否可编辑
     */
    @TableField
    private boolean editable = false;

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemValue() {
        return itemValue;
    }

    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getSortId() {
        return sortId;
    }

    public void setSortId(int sortId) {
        this.sortId = sortId;
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

}
