package com.diboot.core.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.diboot.core.util.JSON;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Entity基础父类
 * @author Mazhicheng
 * @version v2.0
 * @date 2018/12/27
 */
public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 10203L;

    /***
     * 默认主键字段id，类型为Long型自增，转json时转换为String
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /***
     * 默认逻辑删除标记，is_deleted=0有效
     */
    @TableLogic
    @JSONField(serialize = false)
    @TableField("is_deleted")
    private boolean deleted = false;

    /***
     * 默认记录创建时间字段，新建时由数据库赋值
     */
    @TableField(update="now()")
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /***
     * 是否为新建
     * @return
     */
    @JSONField(serialize = false)
    public boolean isNew(){
        return getId() != null;
    }

    /***
     * model对象转为map
     * @return
     */
    public Map<String, Object> toMap(){
        String jsonStr = JSON.stringify(this);
        return JSON.toMap(jsonStr);
    }

    /**
     * model对象转为String
     * @return
     */
    @Override
    public String toString(){
        return this.getClass().getName()+ ":"+this.getId();
    }
}