package com.example.api.handler;

import com.diboot.core.data.protect.DefaultDataEncryptHandler;

/**
 * 默认的加解密实现处理
 * @author JerryMa
 * @version v3.0.0
 * @date 2022/9/9
 * Copyright © diboot.com
 */
//@Component
public class DefaultEncryptHandler extends DefaultDataEncryptHandler {

    @Override
    protected String getSeed() {
        return "Dbt2024";
    }

}