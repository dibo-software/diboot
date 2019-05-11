package com.diboot.core.util;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 原生SQL执行类
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/1/31
 */
public class SqlExecutor {
    private static final Logger log = LoggerFactory.getLogger(SqlExecutor.class);

    /***
     * 执行Select语句
     * @param sql
     * @return
     */
    public static List<Map<String,Object>> executeQuery(String sql, List params) throws Exception{
        if(V.isEmpty(sql)){
            return null;
        }
        // 获取SqlSessionFactory实例
        SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) ContextHelper.getBean(SqlSessionFactory.class);
        if(sqlSessionFactory == null){
            log.warn("无法获取SqlSessionFactory实例，SQL将不被执行。");
            return null;
        }
        // 替换单个?参数为多个，用于拼接IN参数
        if(sql.contains("?") && V.notEmpty(params)){
            sql = S.replace(sql, "?", S.repeat("?", ",", params.size()));
        }
        log.debug("执行查询SQL: "+sql);
        try(SqlSession session = sqlSessionFactory.openSession(); Connection conn = session.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){
            if(V.notEmpty(params)){
                for(int i=0; i<params.size(); i++){
                    stmt.setObject(i+1, params.get(i));
                }
            }
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            List<Map<String,Object>> mapList = new ArrayList<>();
            if(meta.getColumnCount() > 0){
                // 添加数据行
                while(rs.next()){
                    Map<String,Object> dataRow = new HashMap<>();
                    for(int i=1; i<=meta.getColumnCount(); i++){
                        dataRow.put(meta.getColumnLabel(i), rs.getObject(i));
                    }
                    mapList.add(dataRow);
                }
                rs.close();
            }
            log.debug("查询结果: "+JSON.stringify(mapList));
            return mapList;
        }
        catch(Exception e){
            log.error("执行Sql查询异常", e);
            throw e;
        }
    }

    /**
     * 执行查询和合并结果
     * @param sql
     * @param params
     * @return
     */
    public static Map<Object, Object> executeQueryAndMergeResult(String sql, List params, String keyName, String valueName){
        List<Map<String,Object>> resultSetMapList = null;
        try {
            resultSetMapList = executeQuery(sql, params);
        }
        catch (Exception e) {
            log.warn("执行查询异常", e);
        }
        // 合并list为map
        Map<Object, Object> resultMap = new HashMap<>();
        if(V.notEmpty(resultSetMapList)){
            for(Map<String, Object> row : resultSetMapList){
                resultMap.put(row.get(keyName), row.get(valueName));
            }
        }
        return resultMap;
    }

}
