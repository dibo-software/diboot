package diboot.core.test.util;

import com.diboot.core.entity.BaseEntity;

public interface TestService <T extends BaseEntity> {

    default T get() {
        return null;
    }

}
