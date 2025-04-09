package diboot.core.test.binder.multi;

import com.diboot.core.binding.annotation.BindCount;
import diboot.core.test.binder.entity.Pet;
import lombok.Getter;
import lombok.Setter;

/**
 * @author JerryMa
 * @version v3.6.0
 * @date 2025/4/9
 */
@Getter @Setter
public class DogVO extends Animal {

    @BindCount(entity = Pet.class, condition = "this.category=category")
    private Long count;

    private String category;

}
