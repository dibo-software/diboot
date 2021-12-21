package com.diboot.core.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

/**
* attachMore 用于加载关联数据传递的DTO格式
* <p>
* [{type: 'T', target: 'category', label: 'name’, value: 'id'}, {type: 'D', target: 'GENDER'}]
* </p>
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2018/12/27
 */
@Getter
@Setter
@Accessors(chain = true)
public class AttachMoreDTO implements Serializable {
    private static final long serialVersionUID = 10301L;

    /**
     * 关联类型
     */
    @Deprecated
    public enum REF_TYPE {
        /**
         * 绑定的是对象
         */
        T,
        /**
         * 绑定的是字典
         */
        D
    }

    /**
     * 关联的类型
     */
    @Deprecated
    private REF_TYPE type;

    /**
     * <h3>需要查询的目标数据<br/>当label 为空时，则为获取字典，否则获取对象</h3>
     * target应为 实体名 或 字典的type{@link com.diboot.core.entity.Dictionary#type}
     */
    @NotNull(message = "查询类型不能为空！")
    private String target;

    /**
     * <h3>需要查询的label字段</h3>
     * 当为空时，则为获取字典，label为{@link com.diboot.core.entity.Dictionary#itemName}
     */
    private String label;

    /**
     * <h3>需要查询的value字段</h3>
     * 当获取对象时，value默认为主键字段；<br/>
     * 当获取字典时，value为{@link com.diboot.core.entity.Dictionary#itemValue}
     */
    private String value;

    /**
     * <h3>需要查询的ext字段</h3>
     * 当获取字典时，ext为表中{@link com.diboot.core.entity.Dictionary#extdata}
     */
    private String ext;

    /**
     * 额外的条件
     */
    private Map<String, Object> additional;

    /**
     * 关键字（用于前端远程搜索label）
     */
    private String keyword;
}
