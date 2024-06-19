/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.diboot.core.util;

import com.baomidou.mybatisplus.annotation.DbType;
import com.diboot.core.exception.InvalidUsageException;
import com.diboot.core.util.sql.*;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SQL文件初始化处理类
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/08/01
 */
public class SqlFileInitializer {
    private static final Logger log = LoggerFactory.getLogger(SqlFileInitializer.class);

    /**
     * 获取初始化SQL路径
     * @param module
     * @return
     */
    public static String getBootstrapSqlPath(String module) {
        return "META-INF/sql/init-" + module + "-mysql.sql";
    }

    /***
     * 初始化安装SQL
     * @return
     */
    public static void initBootstrapSql(Class inst, String module){
        String mysqlPath = getBootstrapSqlPath(module);
        extractAndExecuteSqls(inst, mysqlPath);
    }

    /**
     * 检查SQL文件是否已经执行过
     * @param sqlStatement
     * @return
     */
    public static boolean checkSqlExecutable(String sqlStatement){
        sqlStatement = buildPureSqlStatement(sqlStatement);
        return SqlExecutor.validateQuery(sqlStatement);
    }

    /**
     * 从sql文件中提取sql语句
     * @param inst 入口执行类
     * @param sqlPath
     * @return
     */
    public static List<String> extractSqlStatements(Class inst, String sqlPath) {
        List<String> sqlFileReadLines = readLinesFromResource(inst, sqlPath);
        if(V.isEmpty(sqlFileReadLines)){
            return Collections.emptyList();
        }
        // 解析SQL
        List<String> sqlStatements = extractSqlStatements(sqlFileReadLines);
        String dbType = getDbType();
        if (DbType.MYSQL.getDb().equalsIgnoreCase(dbType) || DbType.MARIADB.getDb().equalsIgnoreCase(dbType)) {
            return sqlStatements;
        }
        // PostgresSql
        else if(DbType.POSTGRE_SQL.getDb().equalsIgnoreCase(dbType) || DbType.KINGBASE_ES.getDb().equalsIgnoreCase(dbType)) {
            return new PostgresSqlTranslator().translate(sqlStatements);
        }
        // SqlServer
        else if(DbType.SQL_SERVER.getDb().equalsIgnoreCase(dbType)) {
            return new SqlServerTranslator().translate(sqlStatements);
        }
        // DM
        else if(DbType.DM.getDb().equalsIgnoreCase(dbType)) {
            return new DMTranslator().translate(sqlStatements);
        }
        // Oracle
        else if(dbType.startsWith(DbType.ORACLE.getDb())) {
            return new OracleTranslator().translate(sqlStatements);
        }
        // Sqlite
        else if(DbType.SQLITE.getDb().equals(dbType)) {
            return new SqliteTranslator().translate(sqlStatements);
        }
        else {
            throw new InvalidUsageException("exception.invalidUsage.sqlFileInitializer.notSupportDbInit", dbType);
        }
    }

    /***
     * 从SQL文件读出的行内容中 提取SQL语句并执行
     * @param sqlPath
     * @return
     */
    public static boolean extractAndExecuteSqls(Class inst, String sqlPath){
        // 解析SQL
        List<String> sqlStatementList = extractSqlStatements(inst, sqlPath);
        if(V.isEmpty(sqlStatementList)){
            return false;
        }
        // 返回解析后的SQL语句
        return executeMultipleUpdateSqls(sqlStatementList);
    }

    /**
     * 从文件读取到的行提取出SQL语句
     * @param sqlFileReadLines
     * @return
     */
    public static List<String> extractSqlStatements(List<String> sqlFileReadLines){
        // 解析SQL
        List<String> sqlStatementList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for(String line : sqlFileReadLines){
            if(line.contains("--")){
                line = line.substring(0, line.indexOf("--"));
            }
            sb.append(" ");
            if(line.contains(";")){
                // 语句结束
                sb.append(line.substring(0, line.indexOf(";")));
                String cleanSql = buildPureSqlStatement(sb.toString());
                sqlStatementList.add(cleanSql);
                sb.setLength(0);
                if(line.indexOf(";") < line.length()-1){
                    String leftSql = line.substring(line.indexOf(";")+1);
                    if(V.notEmpty(leftSql)){
                        sb.append(leftSql);
                    }
                }
            }
            else if(V.notEmpty(line)){
                sb.append(line);
            }
        }
        if(sb.length() > 0){
            String cleanSql = buildPureSqlStatement(sb.toString());
            sqlStatementList.add(cleanSql);
            sb.setLength(0);
        }
        return sqlStatementList;
    }

    /**
     * 构建纯净可执行的SQL语句: 去除注释，替换变量
     * @param sqlStatement
     * @return
     */
    public static String buildPureSqlStatement(String sqlStatement){
        sqlStatement = clearComments(sqlStatement);
        // 替换sqlStatement中的变量，如{SCHEMA}
        if(sqlStatement.contains("${SCHEMA}")){
            String schema = SqlExecutor.getDatabase();;
            if (V.isEmpty(schema)) {
                sqlStatement = S.replace(sqlStatement, "${SCHEMA}.", "");
            }
            else {
                sqlStatement = S.replace(sqlStatement, "${SCHEMA}", schema);
            }
        }
        return sqlStatement;
    }

    /***
     * 执行多条批量更新SQL（无事务，某条报错不影响后续执行）
     * @param sqlStatementList
     * @return
     */
    public static boolean executeMultipleUpdateSqls(List<String> sqlStatementList){
        if(V.isEmpty(sqlStatementList)){
            return false;
        }
        for(String sqlStatement : sqlStatementList){
            try{
                boolean success = SqlExecutor.executeUpdate(sqlStatement, null);
                if(success){
                    log.info("SQL执行完成: {} ...", S.substring(sqlStatement, 0, 60));
                }
            }
            catch (Exception e){
                log.error("SQL执行异常，请检查或手动执行。SQL => {}", sqlStatement, e);
            }
        }
        return true;
    }

    /***
     * 执行多条批量更新SQL，并在执行异常时跑出异常（支持事务，有报错即回滚）
     * @param sqlStatementList
     * @return
     */
    public static boolean executeMultipleUpdateSqlsWithTransaction(List<String> sqlStatementList) throws Exception {
        if(V.isEmpty(sqlStatementList)){
            return false;
        }
        // 获取SqlSessionFactory实例
        SqlSessionFactory sqlSessionFactory = ContextHolder.getBean(SqlSessionFactory.class);
        if (sqlSessionFactory == null){
            log.warn("无法获取SqlSessionFactory实例，SQL将不被执行。");
            return false;
        }
        SqlSession session = SqlSessionUtils.getSqlSession(sqlSessionFactory);
        Connection conn = session.getConnection();
        try{
            for(String sqlStatement : sqlStatementList){
                PreparedStatement stmt = conn.prepareStatement(sqlStatement);
                stmt.execute();
                stmt.close();
            }
            return true;
        }
        catch (Exception e){
            log.error("SQL执行异常，请检查：{}", sqlStatementList, e);
            if(conn != null) {
                conn.rollback();
            }
            throw e;
        }
    }

    /***
     * 获取
     * @param inst
     * @return
     */
    public static List<String> readLinesFromResource(Class inst, String sqlPath){
        List<String> lines = null;
        try{
            InputStream is = inst.getClassLoader().getResourceAsStream(sqlPath);
            lines = S.readLines(is, "UTF-8");
        }
        catch (FileNotFoundException fe){
            log.warn("暂未发现数据库SQL: {}， 请参考其他数据库定义DDL手动初始化。", sqlPath);
        }
        catch (Exception e){
            log.warn("读取SQL文件异常: {}", sqlPath, e);
        }
        return lines;
    }

    /***
     * 剔除SQL中的注释，提取可执行的实际SQL
     * @param inputSql
     * @return
     */
    public static String clearComments(String inputSql){
        String[] sqlRows = inputSql.split("\\n");
        List<String> cleanSql = new ArrayList();
        for(String row : sqlRows){
            // 去除行注释
            if(row.contains("--")){
                row = row.substring(0, row.indexOf("--"));
            }
            if(V.notEmpty(row.trim())){
                cleanSql.add(row);
            }
        }
        inputSql = S.join(cleanSql, " ");

        // 去除多行注释
        inputSql = removeMultipleLineComments(inputSql);
        // 去除换行
        return inputSql.replaceAll("\r|\n", " ");
    }

    /***
     * 去除多行注释
     * @param inputSql
     * @return
     */
    private static String removeMultipleLineComments(String inputSql){
        if(inputSql.contains("*/*")){
            //忽略此情况，避免死循环
            return inputSql;
        }
        if(inputSql.contains("/*") && inputSql.contains("*/")){
            inputSql = inputSql.substring(0, inputSql.indexOf("/*")) + inputSql.substring(inputSql.indexOf("*/")+2);
        }
        if(inputSql.contains("/*") && inputSql.contains("*/")){
            return removeMultipleLineComments(inputSql);
        }
        return inputSql;
    }

    /**
     * 获取数据库类型
     * @return
     */
    public static String getDbType(){
        return ContextHolder.getDatabaseType();
    }

}