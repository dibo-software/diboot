package com.diboot.core.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 数据字典实体
 * @author Mazhicheng
 * @version v2.0
 * @date 2018/12/27
 */
public class Dictionary extends BaseExtEntity {
    private static final long serialVersionUID = 11301L;

    /***
     * 上级ID
     */
    @NotNull(message = "上级ID不能为空，如无请设为0")
    @TableField
    private Long parentId = 0L;

    /***
     * 数据字典类型
     */
    @NotNull(message = "数据字典类型不能为空！")
    @Length(max = 50, message = "数据字典类型长度超长！")
    @TableField
    private String type;

    /***
     * 数据字典项的显示名称
     */
    @NotNull(message = "数据字典项名称不能为空！")
    @Length(max = 100, message = "数据字典项名称长度超长！")
    @TableField
    private String itemName;

    /***
     * 数据字典项的存储值（编码）
     */
    @Length(max = 100, message = "数据字典项编码长度超长！")
    @TableField
    private String itemValue;

    /***
     * 备注信息
     */
    @Length(max = 200, message = "数据字典备注长度超长！")
    @TableField
    private String description;

    /***
     * 排序号
     */
    @TableField
    private int sortId = 99;

    /***
     * 是否为系统预置（预置不可删除）
     */
    @TableField("is_deletable")
    private boolean deletable = false;

    /***
     * 是否可编辑
     */
    @TableField("is_editable")
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSortId() {
        return sortId;
    }

    public void setSortId(int sortId) {
        this.sortId = sortId;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

}
