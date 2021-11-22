package com.diboot.core.dto;

import com.diboot.core.entity.ValidList;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 级联DTO
 * @author : uu
 * @version : v1.0
 * @Date 2021/11/22  14:20
 */
@Getter@Setter
public class CascaderDTO implements Serializable {
    private static final long serialVersionUID = -3659986749020101170L;

    /**
     * 级联触发值
     */
    private Object triggerValue;

    /**
     * 级联目标数据配置(entity名称)
     */
    @NotNull(message = "级联目标不能为空")
    private List<String> targetList;

    /**
     * 扩展参数
     */
    private Map<String, Object> extParams;
}
