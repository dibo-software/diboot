package com.diboot.example.listener;

import com.diboot.core.util.V;
import com.diboot.shiro.authz.storage.AuthorizationStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * 示例监听器,权限入库
 * @author : wee
 * @version : v 2.0
 * @Date 2019-06-18  23:11
 */
@Component
public class ExampleListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private AuthorizationStorage authorizationStorage;

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (V.isEmpty(event.getApplicationContext().getParent())) {
            authorizationStorage.autoStorage(event.getApplicationContext());
        }
    }
}
