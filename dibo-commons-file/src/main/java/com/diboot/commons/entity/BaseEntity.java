package com.diboot.commons.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.diboot.commons.utils.JSON;
import com.diboot.commons.utils.V;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Entity基础父类
 * @author Lishuaifei
 * @version v2.0
 * @date 2018/12/27
 */
public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 10203L;

    @NotNull(message = "编号不能为空！")
    @Length(max = 32, message = "编号长度超出了最大限制！")
    @TableId(type = IdType.UUID)
    private String uuid = null;

    /***
     * 默认逻辑删除标记，deleted=0有效
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
     * model对象转为map
     * @return
     */
    public Map<String, Object> toMap(){
        String jsonStr = JSON.stringify(this);
        return JSON.toMap(jsonStr);

    }

}