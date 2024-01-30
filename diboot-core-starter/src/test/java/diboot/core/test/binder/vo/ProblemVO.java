package diboot.core.test.binder.vo;

import com.diboot.core.binding.annotation.BindField;
import diboot.core.test.binder.entity.Problem;
import diboot.core.test.binder.entity.StatusInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * 问题VO
 */
@Getter @Setter
public class ProblemVO extends Problem {

    @BindField(entity = StatusInfo.class, field = "userId", condition = "this.id=problem_id")
    private String userId;

}
