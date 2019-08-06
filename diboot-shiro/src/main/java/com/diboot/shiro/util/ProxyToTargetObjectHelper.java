package com.diboot.shiro.util;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Field;

/**
 * 代理对象转成目标对象
 * @author : wee
 * @version : v 2.0
 * @Date 2019-06-19  14:42
 */
public class ProxyToTargetObjectHelper {

    /**
     * 获取代理对象的目标对象
     * 对象可能被多次代理，所以需要递归获取原始对象
     * @param proxy
     * @return
     * @throws Exception
     */
    public static Object getTarget(Object proxy) throws Exception {
        /*判断是否是代理对象*/
        if(!AopUtils.isAopProxy(proxy)) {
            return proxy;
        }
        //判断是jdk动态代理还是cglib代理
        if(AopUtils.isJdkDynamicProxy(proxy)) {
            proxy = getJdkDynamicProxyTargetObject(proxy);
        }
        //cglib
        else {
            proxy = getCglibProxyTargetObject(proxy);
        }
        return getTarget(proxy);
    }


    /**
     * 获取cglib代理的目标对象
     * @param proxy
     * @return
     * @throws Exception
     */
    private static Object getCglibProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        return getJdkOrCglibTargetObject(h, proxy);
    }


    /**
     * 获取jdk动态代理的目标对象
     * @param proxy
     * @return
     * @throws Exception
     */
    private static Object getJdkDynamicProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
        return getJdkOrCglibTargetObject(h, proxy);
    }

    /**
     * 根据不用的代理获取目标对象
     * @param field
     * @param proxy
     * @return
     * @throws Exception
     */
    private static Object getJdkOrCglibTargetObject(Field field, Object proxy) throws Exception{
        field.setAccessible(true);
        Object dynamic = field.get(proxy);
        Field advised;
        //如果是jdk代理
        if (dynamic instanceof AopProxy) {
            advised = ((AopProxy)dynamic).getClass().getDeclaredField("advised");
        }
        //如果是cglib代理
        advised = dynamic.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        //获取目标类
        return ((AdvisedSupport)advised.get(dynamic)).getTargetSource().getTarget();
    }
}
