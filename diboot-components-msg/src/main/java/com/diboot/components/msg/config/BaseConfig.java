package com.diboot.components.msg.config;

/*
 * 配置基类，之后的邮件，短信等实现时都需要需要继承此类
 * 并在此基础上扩展
* */
public abstract class BaseConfig {

    //关键配置是否为空
    public abstract boolean isEmpty();
}
