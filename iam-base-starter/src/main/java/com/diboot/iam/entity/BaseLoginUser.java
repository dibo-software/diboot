package com.diboot.iam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.entity.BaseEntity;
import com.diboot.core.vo.KeyValue;

/**
 * 可登录用户Base类定义
 * @author mazc@dibo.ltd
 * @version v2.1.0
 * @date 2020/06/28
 */
public abstract class BaseLoginUser extends BaseEntity {

    /**
     * 获取显示名称
     * @return
     */
    public abstract String getDisplayName();

    /**
     * 附加对象，用于岗位等扩展
      */
    @TableField(exist = false)
    private KeyValue extentionObj;

    public KeyValue getExtentionObj(){
        return this.extentionObj;
    }
    public void setExtentionObj(KeyValue extentionObj){
        this.extentionObj = extentionObj;
    }

}