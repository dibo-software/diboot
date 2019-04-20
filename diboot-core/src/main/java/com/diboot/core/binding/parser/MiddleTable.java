package com.diboot.core.binding.parser;

import com.diboot.core.config.Cons;

import java.util.ArrayList;
import java.util.List;

/**
 * <Description> <br>
 *
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
    private String leftHandColumn;
    /**
     * 与被引用Entity属性主键的连接字段
     */
    private String rightHandColumn;
    /**
     * 附加条件
     */
    private List<String> additionalConditions;

    public MiddleTable(String table){
        this.table = table;
    }

    public String getTable() {
        return table;
    }

    /**
     * 连接（左手连接VO的外键，右手连接Entity属性的主键）
     * @param leftHandColumn
     * @param rightHandColumn
     * @return
     */
    public MiddleTable connect(String leftHandColumn, String rightHandColumn) {
        this.leftHandColumn = leftHandColumn;
        this.rightHandColumn = rightHandColumn;
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

    /**
     * 转换查询SQL
     * @param annoForeignKeyList 注解外键值的列表，用于拼接SQL查询
     * @return
     */
    public String toSQL(List annoForeignKeyList){
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ").append(this.leftHandColumn).append( Cons.SEPARATOR_COMMA)
          .append(this.rightHandColumn).append(" FROM ").append(this.table)
          .append(" WHERE ").append(this.leftHandColumn).append(" IN(")
          //TODO 生成IN
          .append(")");
        if(this.additionalConditions != null){
            for(String condition : this.additionalConditions){
                sb.append(" AND ").append(condition);
            }
        }
        return sb.toString();
    }

}
