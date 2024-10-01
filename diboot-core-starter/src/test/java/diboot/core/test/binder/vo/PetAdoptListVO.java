package diboot.core.test.binder.vo;

import com.diboot.core.binding.annotation.BindEntityList;
import com.diboot.core.binding.annotation.BindField;
import diboot.core.test.binder.entity.Pet;
import diboot.core.test.binder.entity.PetAdopt;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
* 领养 VO定义
* @author MyName
* @version 1.0
* @date 2024-09-03
* Copyright © MyCorp
*/
@Getter @Setter @Accessors(chain = true)
public class PetAdoptListVO extends PetAdopt {
private static final long serialVersionUID = 1900231745123606236L;

    @BindEntityList(entity = Pet.class, condition = "this.pet_category=category AND this.pet_code=code")
    private List<Pet> pets;

}