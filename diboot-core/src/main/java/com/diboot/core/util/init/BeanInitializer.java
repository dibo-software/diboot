package com.diboot.core.util.init;

/**
 * 对象 初始化器 接口
 * <p>仿照 {@link org.apache.commons.lang3.concurrent.ConcurrentInitializer} 的初始化器接口,
 * 优点是 {@link #get()} 不会抛出受检异常
 * </p>
 *
 * @author Zjp
 * @date 2022/7/15
 * @see org.apache.commons.lang3.concurrent.ConcurrentInitializer
 */
public interface BeanInitializer<T> {
    /**
     * 返回创建好的对象实例, 该方法可能会阻塞, 保证多次调用该方法获取的都是同一实例
     *
     * @return 创建好的对象实例
     * @implSpec 子类 必须保证返回的是同一对象实例
     */
    T get();
}
