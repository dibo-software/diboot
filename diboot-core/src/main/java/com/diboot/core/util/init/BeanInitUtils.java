
package com.diboot.core.util.init;

import com.diboot.core.util.ContextHelper;
import lombok.SneakyThrows;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * 初始化器 工具类
 * <p>封装了 {@link BeanInitializer} , 提供一些简单的使用方法</p>
 *
 * @author Zjp
 * @date 2022/7/14
 */
public class BeanInitUtils {
    protected BeanInitUtils() {
    }

    /**
     * 获取 线程安全的 懒加载的 初始器
     *
     * @param supplier 提供真正的对象
     * @param <T>      真正的对象类型
     * @return 懒加载初始器
     */
    public static <T> BeanInitializer<T> lazyInit(Supplier<T> supplier) {
        return new LazyBeanInitializer<T>() {
            @Override
            protected T initialize() {
                return supplier.get();
            }
        };
    }

    /**
     * 获取 线程安全的 懒加载的 初始化器
     * <p>默认根据类型查找bean</p>
     *
     * @param beanClass bean的class
     * @param <T>       bean类型
     * @return 懒加载初始器
     */
    public static <T> BeanInitializer<T> lazyInit(Class<T> beanClass) {
        return new LazyBeanInitializer<T>() {
            @Override
            protected T initialize() {
                return Objects.requireNonNull(ContextHelper.getBean(beanClass), () -> "找不到class对应的bean: " + beanClass.getName());
            }
        };
    }

    /**
     * 使用 双重校验 实现 懒加载
     * <p>实现参照 {@link org.apache.commons.lang3.concurrent.LazyInitializer} </p>
     *
     * @param <T> 封装的数据对象类型
     */
    private static abstract class LazyBeanInitializer<T> implements BeanInitializer<T> {
        /**
         * 标记未初始化的 默认值
         */
        private static final Object NO_INIT = new Object();

        @SuppressWarnings("unchecked")
        // Stores the managed object.
        private volatile T object = (T) NO_INIT;

        /**
         * Returns the object wrapped by this instance. On first access the object
         * is created. After that it is cached and can be accessed pretty fast.
         *
         * @return the object initialized by this {@code LazyInitializer}
         */
        @SneakyThrows
        @Override
        public T get() {
            // use a temporary variable to reduce the number of reads of the
            // volatile field
            T result = object;

            if (result == NO_INIT) {
                synchronized (this) {
                    result = object;
                    if (result == NO_INIT) {
                        object = result = initialize();
                    }
                }
            }

            return result;
        }

        /**
         * Creates and initializes the object managed by this {@code
         * LazyInitializer}. This method is called by {@link #get()} when the object
         * is accessed for the first time. An implementation can focus on the
         * creation of the object. No synchronization is needed, as this is already
         * handled by {@code get()}.
         *
         * @return the managed data object
         * @throws Exception if an error occurs during object creation
         */
        protected abstract T initialize() throws Exception;
    }

}
