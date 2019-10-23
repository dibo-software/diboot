package com.diboot.shiro.authz.storage;

import com.diboot.core.util.V;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * 权限入库
 * @author : wee
 * @version : v1.0
 * @Date 2019-08-28  10:44
 */
@Setter
@Getter
public class StorageListener implements ApplicationListener<ContextRefreshedEvent> {

    private AuthorizationStorage authorizationStorage;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //容器加载多次，需要判断根容器父级是不是为空，或者祖父级别，为空的时候
        ApplicationContext parent = event.getApplicationContext().getParent();
        if (V.isEmpty(parent) ||
                (V.notEmpty(parent) && V.isEmpty(parent.getParent()))){
            authorizationStorage.autoStorage(event.getApplicationContext());
        }
    }
}
