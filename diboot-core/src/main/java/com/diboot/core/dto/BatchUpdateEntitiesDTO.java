package com.diboot.core.dto;

import com.diboot.core.entity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 批量更新实体DTO
 *
 * @author Yangzhao
 * @version 2.0.0
 * @date 2020/12/8 10:07 上午
 * Copyright © diboot.com
 */
@Getter
@Setter
@Accessors(chain = true)
public class BatchUpdateEntitiesDTO<T extends AbstractEntity<ID_TYPE>, ID_TYPE extends Serializable> implements Serializable {
    private static final long serialVersionUID = -3141680773920758263L;

    private List<ID_TYPE> idList;

    private T data;
}
