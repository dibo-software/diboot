package com.diboot.shiro.wx.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.entity.BaseEntity;
import lombok.Data;

/***
 * @author Wangyl
 * @version v2.0
 * @date 2019/6/10
 */
@Data
public class WxMpMember extends BaseEntity {

    private static final long serialVersionUID = -106928701430810778L;

    @TableField
    private String openid;

}
