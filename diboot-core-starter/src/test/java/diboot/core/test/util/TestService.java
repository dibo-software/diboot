package diboot.core.test.util;

import com.diboot.core.entity.AbstractEntity;
import com.diboot.core.entity.BaseEntity;

public interface TestService <T extends AbstractEntity<String>> {

    default T get() {
        return null;
    }

}
