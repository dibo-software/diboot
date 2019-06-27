package com.diboot.example.listener;

import com.diboot.shiro.authz.storage.EnableStorageEnum;
import com.diboot.shiro.authz.storage.EnvEnum;
import com.diboot.shiro.listener.AbstractStorageApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * 示例监听器
 * {@link AbstractStorageApplicationListener}丰富了 {@link org.springframework.context.ApplicationListener}
 * {@link AbstractStorageApplicationListener}中封装了将{@link com.diboot.shiro.authz.annotation.AuthorizationWrapper}权限自动入库的操作
 * 当你使用注解{@link com.diboot.shiro.authz.annotation.AuthorizationWrapper}，建议直接继承{@link AbstractStorageApplicationListener}
 * 然后对{@link AbstractStorageApplicationListener#customExecute(ContextRefreshedEvent)}方法进行重写
 * 注：需要手动设置一个默认构造函数，传递是否自动权限入库
 * @author : wee
 * @version : v 2.0
 * @Date 2019-06-18  23:11
 */
@Component
public class ExampleListener extends AbstractStorageApplicationListener {

    /**需要手动实现构造来设置是否开启权限入库操作，默认入库*/
    protected ExampleListener() {
        super(EnableStorageEnum.TRUE, EnvEnum.DEV);
    }

    /**
     * 系统启动后，客户自定义事件
     *
     * @param event
     */
    @Override
    protected void customExecute(ContextRefreshedEvent event) {
        System.out.println("============00000000");
    }
}
