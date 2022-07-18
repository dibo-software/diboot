package com.diboot.core.dto;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.controller.BaseController;
import com.diboot.core.util.S;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;
import java.util.function.Function;

/**
 * 用于加载关联数据传递的DTO格式
 *
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2018/12/27
 */
@Getter
@Setter
@Accessors(chain = true)
public class RelatedDataDTO implements Serializable {
    private static final long serialVersionUID = 10301L;

    /**
     * <h3>需要查询的目标对象类型</h3>
     */
    @NotNull(message = "类型不能为空！")
    private String type;

    /**
     * <h3>需要查询的label字段</h3>
     */
    @NotNull(message = "label不能为空！")
    private String label;

    /**
     * <h3>需要查询的value字段</h3>
     * 默认为主键字段
     */
    private String value;

    /**
     * <h3>需要查询的ext字段</h3>
     */
    private String ext;

    /**
     * <h3>筛选条件</h3>
     * 可重写{@link BaseController#buildRelatedDataCondition(RelatedDataDTO, QueryWrapper, Function)}进行自定义筛选条件规则
     */
    private Map<String, Object> condition;

    /**
     * <h3>排序</h3>
     * 示例 `id:DESC,age:ASC`
     */
    private String orderBy;

    /**
     * <h3>父级关联属性</h3>
     * 存储关联数据的属性
     */
    private String parent;

    /**
     * <h3>是否构建树</h3>
     * 仅且第一层生效
     */
    private boolean tree;

    /**
     * <h3>异步加载</h3>
     * 推荐异步加载，默认为true；为false时会同步加载下一级，且当为树时会加载整个树
     */
    private boolean lazy = true;

    /**
     * <h3>下一层</h3>
     */
    private RelatedDataDTO next;

    @JsonIgnore
    public String getTypeClassName(){
        return S.capFirst(S.toLowerCaseCamel(this.type));
    }

}
