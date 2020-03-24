package com.diboot.core.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

/**
 * 配置文件工具类
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/01/01
 */
@Slf4j
public class PropertiesUtils {

    /**
     * Spring配置环境变量
     */
    private static Environment environment;

    /***
     *  读取配置项的值
     * @param key
     * @return
     */
    public static String get(String key){
        // 获取配置值
        if(environment == null){
            environment = ContextHelper.getApplicationContext().getEnvironment();
        }
        if(environment == null){
            log.warn("无法获取上下文Environment !");
            return null;
        }
        String value = environment.getProperty(key);
        // 任何password相关的参数需解密
        boolean isSensitiveConfig = key.contains(".password") || key.contains(".secret");
        if(value != null && isSensitiveConfig){
            value = Encryptor.decrypt(value);
        }
        return value;
    }

    /***
     *  读取int型的配置项
     * @param key
     * @return
     */
    public static Integer getInteger(String key){
        // 获取配置值
        String value = get(key);
        if(V.notEmpty(value)){
            return Integer.parseInt(value);
        }
        return null;
    }

    /***
     * 读取boolean值的配置项
     */
    public static boolean getBoolean(String key) {
        // 获取配置值
        String value = get(key);
        if(V.notEmpty(value)){
            return V.isTrue(value);
        }
        return false;
    }
}
