package com.diboot.components.msg.config;

import com.diboot.core.util.Encryptor;
import com.diboot.core.util.V;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author : wangyongliang@dibo.ltd
 * @version v2.0
 * @Description: 读取配置文件类
 * @Date 2019-07-8
 */
@Configuration
@EnableConfigurationProperties(EmailConfig.class)
@ConfigurationProperties("email.sender")
@Data
public class EmailConfig extends BaseConfig {

    private String name;

    private String address;

    private String password;

    private String host;

    private String sslport;

    @Override
    public boolean isEmpty() {
        if(V.notEmpty(address) && V.notEmpty(password)){
            return false;
        }
        return true;
    }
}
