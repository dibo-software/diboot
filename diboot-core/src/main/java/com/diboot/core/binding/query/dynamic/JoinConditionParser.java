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
package com.diboot.core.binding.query.dynamic;

import com.diboot.core.binding.parser.ConditionManager;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Join条件表达式的管理器
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/4/1
 */
public class JoinConditionParser {
    private static final Logger log = LoggerFactory.getLogger(JoinConditionParser.class);

    /**
     * 解析condition条件
     * @param condition
     * @param alias
     * @throws Exception
     */
    public static String parseJoinCondition(String condition, String alias) {
        List<Expression> expressionList = ConditionManager.getExpressionList(condition);
        if(V.isEmpty(expressionList)){
            log.warn("无法解析注解条件: {} ", condition);
            return null;
        }
        // 解析join
        return parseJoinOn(alias, expressionList);
    }

    /**
     * 解析直接关联
     * @param alias
     * @param expressionList
     */
    private static String parseJoinOn(String alias, List<Expression> expressionList) {
        List<String> segments = new ArrayList<>();
        // 解析直接关联
        for(Expression operator : expressionList){
            if(operator instanceof BinaryExpression){
                BinaryExpression expression = (BinaryExpression)operator;
                String left = formatLeft(expression.getLeftExpression().toString());
                String right = formatRight(expression.getRightExpression().toString());
                if(expression.getRightExpression() instanceof Column){
                    right = alias + "." + right;
                }
                if(operator instanceof EqualsTo){
                    segments.add(left + " = " + right);
                }
                else if(operator instanceof NotEqualsTo){
                    segments.add(left + " != " + right);
                }
                else if(operator instanceof GreaterThan){
                    segments.add(left + " > " + right);
                }
                else if(operator instanceof GreaterThanEquals){
                    segments.add(left + " >= " + right);
                }
                else if(operator instanceof MinorThan){
                    segments.add(left + " < " + right);
                }
                else if(operator instanceof MinorThanEquals){
                    segments.add(left + " <= " + right);
                }
                else{
                    log.warn("暂不支持的条件: "+ expression.toString());
                }
            }
            else if(operator instanceof IsNullExpression){
                IsNullExpression expression = (IsNullExpression)operator;
                String left = formatLeft(expression.getLeftExpression().toString());
                if(expression.isNot() == false){
                    segments.add(left + " IS NULL");
                }
                else{
                    segments.add(left + " IS NOT NULL");
                }
            }
            else if(operator instanceof InExpression){
                InExpression expression = (InExpression)operator;
                String left = formatLeft(expression.getLeftExpression().toString());
                if(expression.isNot() == false){
                    segments.add(left + " IN " + expression.getRightItemsList().toString());
                }
                else{
                    segments.add(left + " NOT IN " + expression.getRightItemsList().toString());
                }
            }
            else if(operator instanceof Between){
                Between expression = (Between)operator;
                String left = formatLeft(expression.getLeftExpression().toString());
                if(expression.isNot() == false){
                    segments.add(left + " BETWEEN " + expression.getBetweenExpressionStart().toString() + " AND " + expression.getBetweenExpressionEnd().toString());
                }
                else{
                    segments.add(left + " NOT BETWEEN " + expression.getBetweenExpressionStart().toString() + " AND " + expression.getBetweenExpressionEnd().toString());
                }
            }
            else if(operator instanceof LikeExpression){
                LikeExpression expression = (LikeExpression)operator;
                String left = formatLeft(expression.getLeftExpression().toString());
                if(expression.isNot() == false){
                    segments.add(left + " LIKE " + expression.getStringExpression());
                }
                else{
                    segments.add(left + " NOT LIKE " + expression.getStringExpression());
                }
            }
            else{
                log.warn("不支持的条件: "+operator.toString());
            }
        }
        if(segments.isEmpty()){
            return null;
        }
        return S.join(segments, " AND ");
    }

    /**
     * 注解列
     * @return
     */
    private static String formatLeft(String annoColumn){
        if(annoColumn.contains("this.")){
            annoColumn = S.replace(annoColumn, "this.", "self.");
        }
        return S.toSnakeCase(annoColumn);
    }

    /**
     * 格式化右侧列
     * @return
     */
    private static String formatRight(String annoColumn){
        return S.toSnakeCase(annoColumn);
    }

}
