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

import com.diboot.core.exception.InvalidUsageException;
import com.diboot.core.util.sql.DoNothingCallbackHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 原生SQL执行类
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/1/31
 */
public class SqlExecutor {
    private static final Logger log = LoggerFactory.getLogger(SqlExecutor.class);

    private static JdbcTemplate jdbcTemplate;
    private static String CURRENT_SCHEMA = null;

    /**
     * 获取 JdbcTemplate 实例
     * @return
     */
    public static JdbcTemplate getJdbcTemplate() {
        if(jdbcTemplate == null) {
            jdbcTemplate = ContextHolder.getBean(JdbcTemplate.class);
        }
        return jdbcTemplate;
    }

    /**
     * 获取数据库名
     * @return
     */
    public static String getDatabase() {
        if(CURRENT_SCHEMA == null) {
            JdbcTemplate jdbcTemplate = getJdbcTemplate();
            DataSource dataSource = jdbcTemplate.getDataSource();
            if(dataSource == null) {
                throw new InvalidUsageException("exception.invalidUsage.sqlExecutor.getDatabase.message");
            }
            try {
                Connection connection = dataSource.getConnection();
                if (connection.getCatalog() != null) {
                    CURRENT_SCHEMA = connection.getCatalog();
                } else if (connection.getSchema() != null) {
                    CURRENT_SCHEMA = connection.getSchema();
                }
                connection.close();
            } catch (Exception e) {
                log.error("获取数据库名异常：{}", e.getMessage());
                return null;
            }
        }
        return CURRENT_SCHEMA;
    }

    /**
     * 检查SQL是否可以正常执行
     * @param sqlStatement
     * @return
     */
    private static DoNothingCallbackHandler doNothingCallbackHandler = new DoNothingCallbackHandler();
    public static boolean validateQuery(String sqlStatement){
        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        if(jdbcTemplate != null) {
            try {
                log.debug("==> {}", sqlStatement);
                jdbcTemplate.query(sqlStatement, doNothingCallbackHandler);
                return true;
            }
            catch (Exception e) {
                log.trace("执行验证SQL:{} 失败:{}", sqlStatement, e.getMessage());
                return false;
            }
        }
        else {
            throw new InvalidUsageException("exception.invalidUsage.sqlExecutor.fetchJdbcTemplateFailed");
        }
    }

    /***
     * 执行Select语句，如: SELECT user_id,role_id FROM user_role WHERE user_id IN (?,?,?,?)
     * 查询结果如: [{"user_id":1001,"role_id":101},{"user_id":1001,"role_id":102},{"user_id":1003,"role_id":102},{"user_id":1003,"role_id":103}]
     * @param sqlStatement
     * @return
     */
    public static List<Map<String,Object>> executeQuery(String sqlStatement, Object... params) throws Exception{
        if(V.isEmpty(sqlStatement)){
            return null;
        }
        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        if(jdbcTemplate != null) {
            try {
                log.debug("==> {}", sqlStatement);
                if(V.isEmpty(params)) {
                    return jdbcTemplate.queryForList(sqlStatement);
                }
                return jdbcTemplate.queryForList(sqlStatement, params);
            }
            catch (Exception e) {
                log.error("执行查询SQL:{} 失败:{}", sqlStatement, e.getMessage());
                return Collections.emptyList();
            }
        }
        else {
            throw new InvalidUsageException("exception.invalidUsage.sqlExecutor.fetchJdbcTemplateFailed");
        }
    }

    /**
     * 执行更新操作
     * @param sqlStatement
     * @param params
     * @return
     * @throws Exception
     */
    public static boolean executeUpdate(String sqlStatement, List params) throws Exception{
        if (V.isEmpty(sqlStatement)){
            return false;
        }
        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        if(jdbcTemplate != null) {
            try {
                log.debug("==> {}", sqlStatement);
                if(V.isEmpty(params)) {
                    jdbcTemplate.execute(sqlStatement);
                    return true;
                }
                return jdbcTemplate.update(sqlStatement, params) >= 0;
            }
            catch (Exception e) {
                log.error("执行更新SQL:{} 失败:{}", sqlStatement, e.getMessage());
                return false;
            }
        }
        else {
            throw new InvalidUsageException("exception.invalidUsage.sqlExecutor.fetchJdbcTemplateFailed");
        }
    }

    /**
     * 执行更新操作
     * @param conn
     * @param sql
     * @param params
     * @return
     * @throws Exception
     */
    public static boolean executeUpdate(Connection conn, String sql, List params) throws Exception{
        log.debug("==>  SQL: {}", sql);
        // 替换单个?参数为多个，用于拼接IN参数
        if(V.notEmpty(params)){
            log.debug("==>  Params: {}", JSON.stringify(params));
            if(params.size() > 2000){
                log.warn("更新参数集合数量过多, size={}，请检查调用是否合理！", params.size());
            }
        }
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            if (V.notEmpty(params)){
                for (int i=0; i<params.size(); i++){
                    stmt.setObject(i + 1, params.get(i));
                }
            }
            return stmt.execute();
        }
        catch(Exception e){
            String sqlInfo = S.substring(sql, 0, 50) + "...";
            log.error("执行sql查询异常: {}", sqlInfo, e);
            throw e;
        }
    }

}
