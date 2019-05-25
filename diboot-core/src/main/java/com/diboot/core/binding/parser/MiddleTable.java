package com.diboot.core.binding.parser;

import com.diboot.core.config.Cons;
import com.diboot.core.util.S;
import com.diboot.core.util.V;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 中间表
 * @author Mazhicheng<br>
 * @version 1.0<br>
 * @date 2019/04/01 <br>
 */
public class MiddleTable {
    /**
     * 中间表
     */
    private String table;
    /**
     * 与注解VO的外键关联的连接字段
     */
    private String equalsToAnnoObjectFKColumn;
    /**
     * 与被引用Entity属性主键的连接字段
     */
    private String equalsToRefEntityPkColumn;
    /**
     * 附加条件
     */
    private List<String> additionalConditions;

    public MiddleTable(String table){
        this.table = table;
    }

    /**
     * 连接（左手连接VO的外键，右手连接Entity属性的主键）
     * @param equalsToAnnoObjectFKColumn
     * @param equalsToRefEntityPkColumn
     * @return
     */
    public MiddleTable connect(String equalsToAnnoObjectFKColumn, String equalsToRefEntityPkColumn) {
        this.equalsToAnnoObjectFKColumn = equalsToAnnoObjectFKColumn;
        this.equalsToRefEntityPkColumn = equalsToRefEntityPkColumn;
        return this;
    }

    /**
     * 添加中间表查询所需的附加条件
     * @param additionalCondition
     */
    public MiddleTable addAdditionalCondition(String additionalCondition) {
        if(this.additionalConditions == null){
            this.additionalConditions = new ArrayList<>();
        }
        this.additionalConditions.add(additionalCondition);
        return this;
    }

    public String getEqualsToAnnoObjectFKColumn() {
        return equalsToAnnoObjectFKColumn;
    }

    public String getEqualsToRefEntityPkColumn() {
        return equalsToRefEntityPkColumn;
    }

    /**
     * 转换查询SQL
     * @param annoObjectForeignKeyList 注解外键值的列表，用于拼接SQL查询
     * @return
     */
    public String toSQL(List annoObjectForeignKeyList){
        if(V.isEmpty(annoObjectForeignKeyList)){
            return null;
        }
        // 构建SQL
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ").append(this.equalsToAnnoObjectFKColumn).append(Cons.SEPARATOR_COMMA)
          .append(this.equalsToRefEntityPkColumn).append(" FROM ").append(this.table)
          .append(" WHERE ").append(this.equalsToAnnoObjectFKColumn).append(" IN (");
        String params = S.repeat("?", ",", annoObjectForeignKeyList.size());
        sb.append(params).append(")");
        if(this.additionalConditions != null){
            for(String condition : this.additionalConditions){
                sb.append(" AND ").append(condition);
            }
        }
        return sb.toString();
    }

}
