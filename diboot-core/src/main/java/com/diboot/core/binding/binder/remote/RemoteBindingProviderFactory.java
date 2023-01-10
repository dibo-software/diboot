package com.diboot.core.binding.binder.remote;

/**
 * 远程绑定工厂类
 * @author JerryMa
 * @version v3.0.0
 * @date 2023/1/10
 * Copyright © diboot.com
 */
public interface RemoteBindingProviderFactory {

    /**
     * 创建provider实例
     * @param module
     * @return
     */
    RemoteBindingProvider create(String module);

}