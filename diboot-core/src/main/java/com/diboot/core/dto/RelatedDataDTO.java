package com.diboot.core.dto;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.controller.BaseController;
import com.diboot.core.data.query.BaseCriteria;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
     * 其value自动取该对象ID值
     */
    @NotNull(message = "{validation.label.NotNull.message}")
    private String type;

    /**
     * <h3>需要查询的label字段</h3>
     */
    @NotNull(message = "{validation.type.NotNull.message}")
    private String label;

    /**
     * <h3>需要查询的ext字段</h3>
     */
    private String ext;

    /**
     * <h3>筛选条件</h3>
     * 可重写{@link BaseController#buildRelatedDataCondition(RelatedDataDTO, QueryWrapper, Function)} (RelatedDataDTO, QueryWrapper, Function)}进行自定义筛选条件规则
     */
    @Deprecated
    private Map<String, Object> condition;

    /**
     * 筛选条件
     */
    private List<BaseCriteria> conditions;

    /**
     * <h3>排序</h3>
     * 示例 `id:DESC,age:ASC`
     */
    private String orderBy;

    /**
     * <h3>用于Tree构数据</h3>
     * 父节点ID属性；如：parentId
     */
    private String parent;

    /**
     * <h3>用于Tree构数据远程过滤</h3>
     * 所有父节点ID拼接属性，便于搜索时向上查找父节点
     */
    private String parentPath;

    /**
     * <h3>异步加载子节点</h3>
     * 推荐异步加载，默认为true；为false时会加载整个树
     */
    private boolean lazyChild = true;

    @JsonIgnore
    public String getTypeClassName(){
        return S.capFirst(S.toLowerCaseCamel(this.type));
    }

    public List<BaseCriteria> getConditions() {
        if(V.isEmpty(conditions) && V.notEmpty(condition)) {
            this.conditions = new ArrayList<>();
            for (Map.Entry<String, Object> item : condition.entrySet()) {
                this.conditions.add(new BaseCriteria(item.getKey(), item.getValue()));
            }
        }
        return conditions;
    }

    /**
     * 移除条件
     * @param field
     */
    public void removeCondition(String field) {
        List<BaseCriteria> criteriaList = getConditions();
        if(V.isEmpty(criteriaList)) {
            return;
        }
        criteriaList.removeIf(criteria -> criteria.getField().equals(field));
    }

}
