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
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SQL文件初始化处理类
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/08/01
 */
public class SqlFileInitializer {
    private static final Logger log = LoggerFactory.getLogger(SqlFileInitializer.class);

    private static String CURRENT_SCHEMA = null;
    private static Environment environment;

    /**
     * 初始化
     * @param env
     */
    public static void init(Environment env) {
        environment = env;
    }

    /**
     * 获取初始化SQL路径
     * @param dbType
     * @param module
     * @return
     */
    public static String getBootstrapSqlPath(String dbType, String module) {
        if (DbType.MARIADB.getDb().equalsIgnoreCase(dbType)) {
            dbType = "mysql";
        }
        else if(DbType.KINGBASE_ES.getDb().equalsIgnoreCase(dbType)) {
            dbType = DbType.POSTGRE_SQL.getDb();
        }
        String sqlPath = "META-INF/sql/init-" + module + "-" + dbType + ".sql";
        return sqlPath;
    }

    /***
     * 初始化安装SQL
     * @return
     */
    public static void initBootstrapSql(Class inst, Environment environment, String module){
        init(environment);
        String dbType = getDbType();
        String sqlPath = getBootstrapSqlPath(dbType, module);
        extractAndExecuteSqls(inst, sqlPath);
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
     * 检查SQL文件是否已经执行过
     * @param sqlStatement
     * @return
     */
    @Deprecated
    public static boolean checkIsTableExists(String sqlStatement){
        return checkSqlExecutable(sqlStatement);
    }

    /***
     * 从SQL文件读出的行内容中 提取SQL语句并执行
     * @param sqlPath
     * @return
     */
    public static boolean extractAndExecuteSqls(Class inst, String sqlPath) {
        return extractAndExecuteSqls(inst, sqlPath, Collections.emptyList(), Collections.emptyList());
    }

    /***
     * 从SQL文件读出的行内容中 提取SQL语句并执行
     * @param sqlPath
     * @param includes
     * @param excludes
     * @return
     */
    public static boolean extractAndExecuteSqls(Class inst, String sqlPath, List<String> includes, List<String> excludes){
        List<String> sqlFileReadLines = readLinesFromResource(inst, sqlPath);
        if(V.isEmpty(sqlFileReadLines)){
            return false;
        }
        // 解析SQL
        List<String> sqlStatementList = extractSqlStatements(sqlFileReadLines);
        // 过滤sql语句
        sqlStatementList = sqlStatementList.stream()
                .filter(sql -> {
                    if (V.isEmpty(includes)) {
                        return true;
                    } else {
                        boolean exist = false;
                        for (String includeStr : includes) {
                            if (V.notEmpty(sql) && sql.contains(includeStr)) {
                                exist = true;
                                break;
                            }
                        }
                        return exist;
                    }
                })
                .filter(sql -> {
                    if (V.isEmpty(excludes)) {
                        return true;
                    } else {
                        boolean exist = true;
                        for (String excludeStr : excludes) {
                            if (V.notEmpty(sql) && sql.contains(excludeStr)) {
                                exist = false;
                                break;
                            }
                        }
                        return exist;
                    }
                })
                .collect(Collectors.toList());
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
            String schema = getCurrentSchema();
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
                    log.info("SQL执行完成: "+ S.substring(sqlStatement, 0, 60) + "...");
                }
            }
            catch (Exception e){
                log.error("SQL执行异常，请检查或手动执行。SQL => "+sqlStatement, e);
            }
        }
        return true;
    }

    /***
     * 执行多条批量更新SQL（支持事务，有报错即回滚）
     * @param sqlStatementList
     * @return
     */
    public static boolean executeMultipleUpdateSqlsWithTransaction(List<String> sqlStatementList){
        try {
            return executeMultipleUpdateSqlsWithTransactionThrowException(sqlStatementList);
        } catch(Exception e) {
            return false;
        }
    }

    /***
     * 执行多条批量更新SQL，并在执行异常时跑出异常（支持事务，有报错即回滚）
     * @param sqlStatementList
     * @return
     */
    public static boolean executeMultipleUpdateSqlsWithTransactionThrowException(List<String> sqlStatementList) throws Exception{
        if(V.isEmpty(sqlStatementList)){
            return false;
        }
        // 获取SqlSessionFactory实例
        SqlSessionFactory sqlSessionFactory = ContextHelper.getBean(SqlSessionFactory.class);
        if (sqlSessionFactory == null){
            log.warn("无法获取SqlSessionFactory实例，SQL将不被执行。");
            return false;
        }
        SqlSession session = sqlSessionFactory.openSession(false);
        try(Connection conn = session.getConnection()){
            conn.setAutoCommit(false);
            for(String sqlStatement : sqlStatementList){
                SqlExecutor.executeUpdate(conn, sqlStatement, null);
            }
            conn.commit();
            return true;
        }
        catch (Exception e){
            log.error("SQL执行异常，请检查：", e);
            session.rollback();
            session.close();
            throw e;
        }
        finally {
            if(session != null){
                session.close();
            }
        }
    }

    /***
     * 获取
     * @param inst
     * @return
     */
    protected static List<String> readLinesFromResource(Class inst, String sqlPath){
        List<String> lines = null;
        try{
            InputStream is = inst.getClassLoader().getResourceAsStream(sqlPath);
            lines = S.readLines(is, "UTF-8");
        }
        catch (FileNotFoundException fe){
            log.warn("暂未发现数据库SQL: "+sqlPath + "， 请参考其他数据库定义DDL手动初始化。");
        }
        catch (Exception e){
            log.warn("读取SQL文件异常: "+sqlPath, e);
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
     * 获取当前schema
     * @return
     */
    public static String getCurrentSchema() {
        if(CURRENT_SCHEMA == null) {
            DataSource dataSource = ContextHelper.getBean(DataSource.class);
            try{
                Connection connection = dataSource.getConnection();
                CURRENT_SCHEMA = connection.getSchema();
                connection.close();
            }
            catch (Exception e){
                log.warn("获取schema异常: {}", e.getMessage());
            }
            if(CURRENT_SCHEMA == null) {
                CURRENT_SCHEMA = "";
            }
        }
        return CURRENT_SCHEMA;
    }

    /**
     * 获取数据库类型
     * @return
     */
    public static String getDbType(){
        return ContextHelper.getDatabaseType();
    }

}