package com.diboot.component.file.starter;

import lombok.extern.slf4j.Slf4j;

/**
 * SQL处理类
 * @author Yangzhao
 * @version v2.0
 * @date 2019/08/01
 */
@Slf4j
public class SqlHandler extends com.diboot.core.starter.SqlHandler {
    // 文件SQL
    private static final String FILE_SQL = "SELECT uuid FROM ${SCHEMA}.upload_file WHERE uuid='xyz'";

    /**
     * 检查file表是否已存在
     * @return
     */
    public static boolean checkIsFileTableExists(){
        return checkIsTableExists(FILE_SQL);
    }
}