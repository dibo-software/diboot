package com.diboot.message.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.entity.BaseEntity;
import com.diboot.core.util.D;
import com.diboot.core.util.JSON;
import com.diboot.core.util.V;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息模版 Entity定义
 *
 * @author : uu
 * @version : v2.0
 * @Date 2021/2/25  09:39
 */
@Getter
@Setter
@Accessors(chain = true)
public class MessageTemplate extends BaseEntity {
    private static final long serialVersionUID = 5255165821023367198L;

    /**
     * 租户id
     */
    @TableField()
    private Long tenantId;

    /**
     * 应用模块
     */
    @Length(max = 50, message = "应用模块长度应小于50")
    @TableField()
    private String appModule;

    /**
     * 模版编码
     */
    @NotNull(message = "模版编码不能为空")
    @Length(max = 20, message = "模版编码长度应小于20")
    @TableField()
    private String code;

    /**
     * 模版标题
     */
    @NotNull(message = "模版标题不能为空")
    @Length(max = 100, message = "模版标题长度应小于100")
    @TableField()
    private String title;

    /**
     * 模版内容
     */
    @NotNull(message = "模版内容不能为空")
    @TableField()
    private String content;

    /**
     * 模版变量
     */
    @Length(max = 200, message = "模版变量长度应小于200")
    @TableField()
    private String variables;

    /**
     * 扩展数据
     */
    @TableField()
    private String extData;

    /**
     * 创建人
     */
    @TableField()
    private Long createBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = D.FORMAT_DATETIME_Y4MDHM)
    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NOT_NULL)
    private Date updateTime;

    /**
     * 扩展字段Map
     */
    @TableField(exist = false)
    private Map<String, Object> extDataMap;


    public Map<String, Object> getExtDataMap() {
        return V.isEmpty(this.extData) ? new HashMap<>(16) : JSON.toMap(this.extData);
    }

    public void setExtDataMap(Map<String, Object> extDataMap) {
        if (V.isEmpty(extDataMap)) {
            extDataMap = new HashMap<>(16);
        }
        this.extDataMap = extDataMap;
        this.extData = JSON.stringify(extDataMap);
    }

} 
