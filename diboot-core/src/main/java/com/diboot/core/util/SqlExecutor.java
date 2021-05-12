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

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
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

    /**
     * 检查SQL是否可以正常执行
     * @param sqlStatement
     * @return
     */
    public static boolean validateQuery(String sqlStatement){
        // 获取SqlSessionFactory实例
        SqlSessionFactory sqlSessionFactory = ContextHelper.getBean(SqlSessionFactory.class);
        if(sqlSessionFactory == null){
            log.warn("无法获取SqlSessionFactory实例，SQL将不被执行。");
            return false;
        }
        try(SqlSession session = sqlSessionFactory.openSession(); Connection conn = session.getConnection(); PreparedStatement stmt = conn.prepareStatement(sqlStatement)){
            ResultSet rs = stmt.executeQuery();
            rs.close();
            log.debug("==> {}", sqlStatement);
            return true;
        }
        catch(Exception e){
            log.trace("执行验证SQL:{} 失败:{}", sqlStatement, e.getMessage());
            return false;
        }
    }

    /***
     * 执行Select语句，如: SELECT user_id,role_id FROM user_role WHERE user_id IN (?,?,?,?)
     * 查询结果如: [{"user_id":1001,"role_id":101},{"user_id":1001,"role_id":102},{"user_id":1003,"role_id":102},{"user_id":1003,"role_id":103}]
     * @param sql
     * @return
     */
    public static <E> List<Map<String,E>> executeQuery(String sql, List<E> params) throws Exception{
        if(V.isEmpty(sql)){
            return null;
        }
        // 获取SqlSessionFactory实例
        SqlSessionFactory sqlSessionFactory = ContextHelper.getBean(SqlSessionFactory.class);
        if(sqlSessionFactory == null){
            log.warn("无法获取SqlSessionFactory实例，SQL将不被执行。");
            return null;
        }
        log.debug("==>  SQL: "+sql);
        // 替换单个?参数为多个，用于拼接IN参数
        if(V.notEmpty(params)){
            log.debug("==>  Params: {}", JSON.stringify(params));
            if(params.size() > 2000){
                log.warn("查询参数集合数量过多, size={}，请检查调用是否合理！", params.size());
            }
        }
        try(SqlSession session = sqlSessionFactory.openSession(); Connection conn = session.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){
            if(V.notEmpty(params)){
                for(int i=0; i<params.size(); i++){
                    stmt.setObject(i+1, params.get(i));
                }
            }
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            List<Map<String,E>> mapList = new ArrayList<>();
            if(meta.getColumnCount() > 0){
                // 添加数据行
                while(rs.next()){
                    Map<String,E> dataRow = new HashMap<>();
                    for(int i=1; i<=meta.getColumnCount(); i++){
                        dataRow.put(meta.getColumnLabel(i), (E)rs.getObject(i));
                    }
                    mapList.add(dataRow);
                }
                rs.close();
            }
            return mapList;
        }
        catch(Exception e){
            log.error("执行Sql查询异常", e);
            throw e;
        }
    }

    /**
     * 执行1-1关联查询和合并结果并将结果Map的key类型转成String
     *
     * @param sql
     * @param params
     * @return
     */
    @Deprecated
    public static <E> Map<String, Object> executeQueryAndMergeOneToOneResult(String sql, List<E> params, String keyName, String valueName) {
        List<Map<String, E>> resultSetMapList = null;
        try {
            resultSetMapList = executeQuery(sql, params);
        } catch (Exception e) {
            log.warn("执行查询异常", e);
        }
        return convertToOneToOneResult(resultSetMapList, keyName, valueName);
    }

    /**
     * 合并为1-1的map结果
     * @param resultSetMapList
     * @param keyName
     * @param valueName
     * @param <E>
     * @return
     */
    @Deprecated
    public static <E> Map<String, Object> convertToOneToOneResult(List<Map<String, E>> resultSetMapList, String keyName, String valueName) {
        // 合并list为map
        Map<String, Object> resultMap = new HashMap<>();
        if(V.notEmpty(resultSetMapList)){
            for(Map<String, E> row : resultSetMapList){
                Object keyObj = row.get(keyName);
                if(keyObj == null && row.get(keyName.toUpperCase()) != null){
                    keyObj = row.get(keyName.toUpperCase());
                }
                Object valueObj = row.get(valueName);
                if(valueObj == null && row.get(valueName.toUpperCase()) != null){
                    valueObj = row.get(valueName.toUpperCase());
                }
                resultMap.put(S.valueOf(keyObj), valueObj);
            }
        }
        return resultMap;
    }

    /**
     * 执行查询和合并结果并将结果Map的key类型转成String
     * @param sql
     * @param params
     * @return
     */
    @Deprecated
    public static <E> Map<String, List> executeQueryAndMergeOneToManyResult(String sql, List<E> params, String keyName, String valueName){
        List<Map<String, E>> resultSetMapList = null;
        try {
            resultSetMapList = executeQuery(sql, params);
        }
        catch (Exception e) {
            log.warn("执行查询异常", e);
        }
        return convertToOneToManyResult(resultSetMapList, keyName, valueName);
    }

    /**
     * 合并为1-n的map结果
     * @param resultSetMapList
     * @param keyName
     * @param valueName
     * @param <E>
     * @return
     */
    @Deprecated
    public static <E> Map<String, List> convertToOneToManyResult(List<Map<String, E>> resultSetMapList, String keyName, String valueName){
        // 合并list为map
        Map<String, List> resultMap = new HashMap<>();
        if(V.notEmpty(resultSetMapList)){
            for(Map<String, E> row : resultSetMapList){
                Object keyObj = row.get(keyName);
                if(keyObj == null && row.get(keyName.toUpperCase()) != null){
                    keyObj = row.get(keyName.toUpperCase());
                }
                String key = S.valueOf(keyObj);
                List valueList = resultMap.get(key);
                if(valueList == null){
                    valueList = new ArrayList();
                    resultMap.put(key, valueList);
                }
                Object valueObj = row.get(valueName);
                if(valueObj == null && row.get(valueName.toUpperCase()) != null){
                    valueObj = row.get(valueName.toUpperCase());
                }
                valueList.add(valueObj);
            }
        }
        return resultMap;
    }

    /**
     * 执行更新操作
     * @param sql
     * @param params
     * @return
     * @throws Exception
     */
    public static boolean executeUpdate(String sql, List params) throws Exception{
        if (V.isEmpty(sql)){
            return false;
        }
        // 获取SqlSessionFactory实例
        SqlSessionFactory sqlSessionFactory = ContextHelper.getBean(SqlSessionFactory.class);
        if (sqlSessionFactory == null){
            log.warn("无法获取SqlSessionFactory实例，SQL将不被执行。");
            return false;
        }
        log.debug("==>  SQL: "+sql);
        // 替换单个?参数为多个，用于拼接IN参数
        if(V.notEmpty(params)){
            log.debug("==>  Params: {}", JSON.stringify(params));
            if(params.size() > 2000){
                log.warn("更新参数集合数量过多, size={}，请检查调用是否合理！", params.size());
            }
        }
        try(SqlSession session = sqlSessionFactory.openSession(); Connection conn = session.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){
            if (V.notEmpty(params)){
                for (int i=0; i<params.size(); i++){
                    stmt.setObject(i + 1, params.get(i));
                }
            }
            int rs = stmt.executeUpdate();
            return rs >= 0;
        } catch(Exception e){
            String sqlInfo = S.substring(sql, 0, 50) + "...";
            log.error("执行sql查询异常: "+sqlInfo, e);
            throw e;
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
        log.debug("==>  SQL: "+sql);
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
            //return rs >= 0;
        }
        catch(Exception e){
            String sqlInfo = S.substring(sql, 0, 50) + "...";
            log.error("执行sql查询异常: "+sqlInfo, e);
            throw e;
        }
    }

}
