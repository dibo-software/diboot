package diboot.core.test.binder.entity;

import com.diboot.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain = true)
public class Problem extends BaseEntity<String> {
    private static final long serialVersionUID = 2701095453152116088L;

    private String sn;

    private String title;

    private String remark;

}
