package diboot.core.test.binder.dto;

import com.diboot.core.binding.query.BindQuery;
import diboot.core.test.binder.entity.Problem;
import diboot.core.test.binder.entity.StatusInfo;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProblemDTO extends Problem {

    @BindQuery(entity = StatusInfo.class, column = "user_id", condition = "this.id=problem_id")
    private String userId;

}
