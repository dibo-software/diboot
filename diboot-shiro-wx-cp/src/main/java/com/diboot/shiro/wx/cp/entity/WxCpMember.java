package com.diboot.shiro.wx.cp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.entity.BaseEntity;
import lombok.Data;

/***
 * @author Wangyl
 * @version v2.0
 * @date 2019/6/10
 */
@Data
public class WxCpMember extends BaseEntity {
    private static final long serialVersionUID = 877938583969333605L;

    @TableField
    private String userId;

}
