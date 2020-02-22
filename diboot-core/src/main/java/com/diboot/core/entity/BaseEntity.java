package com.diboot.core.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import com.diboot.core.config.Cons;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.JSON;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Entity基础父类
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2018/12/27
 */
@Getter
@Setter
@Accessors(chain = true)
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
    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private Date createTime;

    /***
     * Entity对象转为map
     * @return
     */
    public Map<String, Object> toMap(){
        String jsonStr = JSON.stringify(this);
        return JSON.toMap(jsonStr);
    }

    /**
     * 获取主键值
     * @return
     */
    @JSONField(serialize = false)
    public Object getPrimaryKey(){
        String pk = ContextHelper.getPrimaryKey(this.getClass());
        if(Cons.FieldName.id.name().equals(pk)){
            return getId();
        }
        return BeanUtils.getProperty(this, pk);
    }

    /**
     * Entity对象转为String
     * @return
     */
    @Override
    public String toString(){
        return this.getClass().getName()+ ":"+this.getId();
    }
}