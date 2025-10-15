package diboot.core.test.binder.dto;

import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import diboot.core.test.binder.entity.Pet;
import diboot.core.test.binder.entity.PetAdopt;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PetDTO extends Pet {

    @BindQuery(field = "realname", comparison = Comparison.LIKE,
            entity = PetAdopt.class, condition = "this.id=pet_code", distinct = false)
    private String petAdoptName;

}
