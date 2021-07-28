package com.diboot.core.handler;

/**
 * 加解密接口
 *
 * @author : uu
 * @version : v1.0
 * @Date 2021/7/13  09:45
 */
public interface IEncryptorHandler {

    /**
     * 解密
     * @param content
     * @return
     * @throws Exception
     */
    default String encrypt(String content) throws Exception{
        return content;
    }

    /**
     * 解密
     * @param content
     * @return
     * @throws Exception
     */
    default String decrypt(String content) throws Exception{
        return content;
    }

}
