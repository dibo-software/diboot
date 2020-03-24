package com.diboot.core.vo;

import com.diboot.core.binding.annotation.BindEntityList;
import com.diboot.core.entity.Dictionary;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 数据字典的VO，附带子项定义children
 */
@Getter
@Setter
@Accessors(chain = true)
public class DictionaryVO extends Dictionary {

    @BindEntityList(entity= Dictionary.class, condition="this.type=type AND parent_id>0")
    private List<Dictionary> children;

}
