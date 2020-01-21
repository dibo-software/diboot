package com.diboot.core.vo;

import com.diboot.core.binding.annotation.BindEntityList;
import com.diboot.core.entity.Dictionary;

import java.util.List;

/**
 * 数据字典的VO，附带子项定义children
 */
public class DictionaryVO extends Dictionary {

    @BindEntityList(entity= Dictionary.class, condition="this.type=type AND parent_id>0")
    private List<Dictionary> children;

    public List<Dictionary> getChildren() {
        return children;
    }

    public void setChildren(List<Dictionary> children) {
        this.children = children;
    }
}
