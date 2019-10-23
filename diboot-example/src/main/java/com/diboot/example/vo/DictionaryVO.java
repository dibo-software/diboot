package com.diboot.example.vo;

import com.diboot.core.binding.annotation.BindEntityList;
import com.diboot.core.entity.Dictionary;
import lombok.Data;

import java.util.List;

@Data
public class DictionaryVO extends Dictionary {

    @BindEntityList(entity=Dictionary.class, condition="this.type=type AND parent_id>0 AND is_deleted=0")
    private List<Dictionary> children;

}
