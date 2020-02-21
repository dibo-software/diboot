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
    private static final String FILE_SQL = "SELECT id FROM ${SCHEMA}.file WHERE id=0";
    // 列定义SQL
    private static final String EXCEL_COLUMN_SQL = "SELECT id FROM ${SCHEMA}.excel_column WHERE id=0";
    // Excel导入记录SQL
    private static final String EXCEL_IMPORT_RECORD_SQL = "SELECT id FROM ${SCHEMA}.excel_import_record WHERE id=0";

    /**
     * 检查file表是否已存在
     * @return
     */
    public static boolean checkIsFileTableExists(){
        return checkIsTableExists(FILE_SQL);
    }
}