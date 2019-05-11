package com.diboot.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 配置文件工具类
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/01/01
 */
public class PropertiesUtils {
    private static final Logger log = LoggerFactory.getLogger(PropertiesUtils.class);

    /***
     * 缓存多个资源文件
      */
    private static Map<String, Properties> allPropertiesMap = new HashMap<>();

    /**
     * 默认资源文件名称
      */
    private static final String DEFAULT_PROPERTIES_NAME = "application.properties";

    /***
     * Properties配置工厂类
     */
    private static PropertiesFactoryBean pfb = new PropertiesFactoryBean();

    /***
     * 获取指定的配置文件
     * @return
     */
    private static Properties getProperties(String... propertiesFileNameArgs){
        // 获取文件名
        String propFileName = getPropertiesFileName(propertiesFileNameArgs);
        // 从缓存读取
        Properties props = allPropertiesMap.get(propFileName);
        if(props == null){
            if(propFileName.endsWith(".properties")){
                pfb.setLocation(new ClassPathResource(propFileName));
                try{
                    pfb.afterPropertiesSet();
                    props = pfb.getObject();
                }
                catch (Exception e){
                    log.warn("无法找到配置文件: " + propFileName, e);
                }
            }
            else if(propFileName.endsWith(".yaml")){
                props = yamlToProperties(propFileName);
            }
            if(props != null){
                allPropertiesMap.put(propFileName, props);
            }
        }
        return props;
    }

    /***
     *  读取配置项的值
     * @param key
     * @return
     */
    public static String get(String key, String... propertiesFileName){
        // 获取文件名
        String propFileName = getPropertiesFileName(propertiesFileName);
        // 获取配置值
        String value = getConfigValueFromPropFile(key, propFileName);
        if(value == null && DEFAULT_PROPERTIES_NAME.equals(propFileName)){
            // 如果是默认配置，读取不到再尝试从当前profile配置中读取
            String profile = getConfigValueFromPropFile("spring.profiles.active", DEFAULT_PROPERTIES_NAME);
            if(V.notEmpty(profile)){
                value = getConfigValueFromPropFile(key, S.substringBeforeLast(DEFAULT_PROPERTIES_NAME, ".") + "-"+profile+".properties");
            }
        }
        if(value == null){
            log.trace("配置文件 {} 中未找到配置项: {}", propertiesFileName, key);
        }
        return value;
    }

    /***
     *  读取int型的配置项
     * @param key
     * @return
     */
    public static Integer getInteger(String key, String... propertiesFileName){
        // 获取文件名
        String propFileName = getPropertiesFileName(propertiesFileName);
        // 获取配置值
        String value = get(key, propFileName);
        if(V.notEmpty(value)){
            return Integer.parseInt(value);
        }
        log.trace("配置文件 {} 中未找到配置项: {}", propertiesFileName, key);
        return null;
    }

    /***
     * 读取boolean值的配置项
     */
    public static boolean getBoolean(String key, String... propertiesFileName) {
        // 获取文件名
        String propFileName = getPropertiesFileName(propertiesFileName);
        // 获取配置值
        String value = get(key, propFileName);
        if(V.notEmpty(value)){
            return V.isTrue(value);
        }
        log.trace("配置文件 "+(V.notEmpty(propertiesFileName)? propertiesFileName[0] : "")+" 中未找到配置项: "+key + ", 启用默认值 false.");
        return false;
    }

    /***
     * 获取配置文件名称
     * @param propertiesFileName
     * @return
     */
    private static String getPropertiesFileName(String... propertiesFileName){
        // 获取文件名
        if(propertiesFileName != null && propertiesFileName.length > 0){
            return propertiesFileName[0];
        }
        else{
            return DEFAULT_PROPERTIES_NAME;
        }
    }

    /***
     * 从配置文件中读取配置值
     * @param key
     * @param propertiesFileName
     * @return
     */
    private static String getConfigValueFromPropFile(String key, String propertiesFileName){
        // 获取配置值
        Properties properties = getProperties(propertiesFileName);
        if(properties != null){
            if(properties.containsKey(key)){
                String value = properties.getProperty(key);
                // 任何password相关的参数需解密
                boolean isSensitiveConfig = key.contains(".password") || key.contains(".secret");
                if(value != null && isSensitiveConfig){
                    value = Encryptor.decrypt(value);
                }
                return value;
            }
        }
        return null;
    }

    /**
     * 读取yaml并转换为Properties
     * @param yamlSource
     * @return
     */
    private static Properties yamlToProperties(String yamlSource) {
        try {
            YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
            yaml.setResources(new ClassPathResource(yamlSource));
            return yaml.getObject();
        }
        catch (Exception e) {
            log.error("Cannot read yaml "+yamlSource, e);
            return null;
        }
    }
}
