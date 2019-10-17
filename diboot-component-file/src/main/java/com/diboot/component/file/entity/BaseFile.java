package com.diboot.component.file.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.diboot.core.entity.BaseEntity;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * file基础父类
 * @author wangyl
 * @version v2.0
 * @date 2019/07/18
 */
@TableName("file")
public class BaseFile extends BaseEntity {
    private static final long serialVersionUID = 201L;

    public enum FILE_STATUS {
            S,
            F
    }
    // 废弃默认主键
    @TableField(exist = false)
    private Long id;

    // 声明新主键uuid
    @NotNull(message = "编号不能为空！")
    @Length(max = 32, message = "编号长度超出了最大限制！")
    @TableId(type = IdType.UUID)
    private String uuid = null;

    @NotNull(message = "关联对象类型不能为空！")
    @Length(max = 50, message = "关联对象类型长度超出了最大限制！")
    @TableField
    private String relObjType = null;
    @TableField
    @NotNull(message = "关联对象ID不能为空！")
    private Long relObjId;

    @TableField
    @NotNull(message = "文件名不能为空！")
    @Length(max = 100, message = "文件名长度超出了最大限制！")
    private String name;

    @TableField
    @NotNull(message = "文件链接不能为空！")
    @Length(max = 255, message = "文件链接长度超出了最大限制！")
    private String link;

    @TableField
    @NotNull(message = "文件路径不能为空！")
    @Length(max = 255, message = "文件路径长度超出了最大限制！")
    private String path;

    @TableField
    @Length(max = 20, message = "文件类型长度超出了最大限制！")
    private String fileType;

    @TableField
    private int dataCount = 0;

    @TableField
    private Long size;

    @TableField
    @Length(max = 1, message = "状态长度超出了最大限制！")
    private String status;

    @TableField
    @Length(max = 255, message = "备注长度超出了最大限制！")
    private String comment;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRelObjType() {
        return this.relObjType;
    }

    public void setRelObjType(String relObjType) {
        this.relObjType = relObjType;
    }

    public Long getRelObjId() {
        return this.relObjId;
    }

    public void setRelObjId(Long relObjId) {
        this.relObjId = relObjId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileType() {
        return this.fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getSize() {
        return this.size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getDataCount() {
        return this.dataCount;
    }

    public void setDataCount(int dataCount) {
        this.dataCount = dataCount;
    }

    public String getStatus() {
        return this.status;
    }

    public String getStatusLabel(){
        if(FILE_STATUS.S.name().equals(this.status)){
            return "成功";
        }else if(FILE_STATUS.F.name().equals(this.status)){
            return "失败";
        }else{
            return null;
        }
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
