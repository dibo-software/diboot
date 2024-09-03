package diboot.core.test.binder.vo;

import com.diboot.core.binding.annotation.BindDict;
import com.diboot.core.binding.annotation.BindField;
import com.diboot.core.vo.LabelValue;
import diboot.core.test.binder.entity.Pet;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
* 宠物 VO定义
* @author MyName
* @version 1.0
* @date 2024-09-03
* Copyright © MyCorp
*/
@Getter @Setter @Accessors(chain = true)
public class PetListVO extends Pet {
private static final long serialVersionUID = 1125570722268444471L;
    /**
    * 品类 关联字典选项
    */
    @BindDict(type = "PET_CATEGORY", field = "category")
    private LabelValue categoryLabel;

}