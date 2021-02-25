package com.diboot.message.dto;

import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.diboot.core.util.D;
import com.diboot.message.entity.MessageTemplate;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;


/**
 * 消息模版 DTO定义
 * @author : uu
 * @version : v2.0
 * @Date 2021/2/25  09:39
 */
@Getter
@Setter
@Accessors(chain = true)
public class MessageTemplateDTO extends MessageTemplate {

    private static final long serialVersionUID = 4601855668034533381L;

    /**
     * 创建时间-起始
     */
    @BindQuery(comparison = Comparison.GE, field = "createTime")
    private Date createTime;

    /**
     * 创建时间-截止
     */
    @BindQuery(comparison = Comparison.LT, field = "createTime")
    private Date createTimeEnd;

    @Override()
    public Date getCreateTime() {
        return this.createTime;
    }

    @Override()
    public MessageTemplate setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public Date getCreateTimeEnd() {
        return D.nextDay(createTime);
    }

    public MessageTemplate setCreateTimeEnd(Date createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
        return this;
    }
}
