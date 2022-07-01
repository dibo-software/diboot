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
package com.diboot.core.binding.parser;

import com.diboot.core.util.S;
import com.diboot.core.util.V;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.conditional.XorExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.SubSelect;

import java.util.ArrayList;
import java.util.List;

/**
 * 关联注解条件解析器
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/3/30
 */
public class ConditionParser implements ExpressionVisitor,ItemsListVisitor {

    public ConditionParser() {
    }

    private List<String> errorMsgList = null;
    private List<Expression> expressList = new ArrayList<>(4);

    /**
     * 添加错误信息
     * @param errorMsg
     */
    private void addError(String errorMsg){
        if(errorMsgList == null){
            errorMsgList = new ArrayList<>();
        }
        if(!errorMsgList.contains(errorMsg)){
            errorMsgList.add(errorMsg);
        }
    }

    /**
     * 获取解析后的结果
     * @return
     * @throws Exception
     */
    public List<Expression> getExpressList() throws Exception{
        if(V.notEmpty(errorMsgList)){
            throw new Exception(S.join(errorMsgList, "; "));
        }
        return expressList;
    }

    @Override
    public void visit(AndExpression andExpression) {
        andExpression.getLeftExpression().accept(this);
        andExpression.getRightExpression().accept(this);
    }

    // ----- 支持的条件
    @Override
    public void visit(EqualsTo equalsTo) {
        if(!(equalsTo.getLeftExpression() instanceof Column)){
            addError("=条件左侧必须为字段/列名");
        }
        expressList.add(equalsTo);
    }
    @Override
    public void visit(NotEqualsTo notEqualsTo) {
        if(!(notEqualsTo.getLeftExpression() instanceof Column)){
            addError("!=条件左侧必须为字段/列名");
        }
        expressList.add(notEqualsTo);
    }
    @Override
    public void visit(GreaterThan greaterThan) {
        if(!(greaterThan.getLeftExpression() instanceof Column)){
            addError(">条件左侧必须为字段/列名");
        }
        expressList.add(greaterThan);
    }
    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {
        if(!(greaterThanEquals.getLeftExpression() instanceof Column)){
            addError(">=条件左侧必须为字段/列名");
        }
        expressList.add(greaterThanEquals);
    }
    @Override
    public void visit(MinorThan minorThan) {
        if(!(minorThan.getLeftExpression() instanceof Column)){
            addError("<条件左侧必须为字段/列名");
        }
        expressList.add(minorThan);
    }
    @Override
    public void visit(MinorThanEquals minorThanEquals) {
        if(!(minorThanEquals.getLeftExpression() instanceof Column)){
            addError("<=条件左侧必须为字段/列名");
        }
        expressList.add(minorThanEquals);
    }
    @Override
    public void visit(IsNullExpression isNullExpression) {
        if(!(isNullExpression.getLeftExpression() instanceof Column)){
            addError("IsNull条件左侧必须为字段/列名");
        }
        expressList.add(isNullExpression);
    }

    @Override
    public void visit(IsBooleanExpression isBooleanExpression) {

    }

    @Override
    public void visit(InExpression inExpression) {
        if(!(inExpression.getLeftExpression() instanceof Column)){
            addError("IN条件左侧必须为字段/列名");
        }
        expressList.add(inExpression);
    }

    @Override
    public void visit(FullTextSearch fullTextSearch) {

    }

    @Override
    public void visit(Between between) {
        if(!(between.getLeftExpression() instanceof Column)){
            addError("Between条件左侧必须为字段/列名");
        }
        expressList.add(between);
    }
    @Override
    public void visit(LikeExpression likeExpression) {
        if(!(likeExpression.getLeftExpression() instanceof Column)){
            addError("Like条件左侧必须为字段/列名");
        }
        expressList.add(likeExpression);
    }

    //------- 暂不支持的条件
    @Override
    public void visit(OrExpression orExpression) {
        addError("暂不支持 OR 关联条件");
    }

    @Override
    public void visit(XorExpression xorExpression) {
        addError("暂不支持 XOR 关联条件");
    }

    // ------ 忽略的条件
    @Override
    public void visit(Column tableColumn) {
    }
    @Override
    public void visit(SubSelect subSelect) {
    }

    @Override
    public void visit(ExpressionList expressionList) {
    }

    @Override
    public void visit(NamedExpressionList namedExpressionList) {
    }

    @Override
    public void visit(MultiExpressionList multiExprList) {
    }
    @Override
    public void visit(CaseExpression caseExpression) {
    }
    @Override
    public void visit(WhenClause whenClause) {
    }
    @Override
    public void visit(ExistsExpression existsExpression) {
    }
    @Override
    public void visit(AnyComparisonExpression anyComparisonExpression) {
    }
    @Override
    public void visit(Concat concat) {
    }
    @Override
    public void visit(Matches matches) {
    }
    @Override
    public void visit(BitwiseAnd bitwiseAnd) {
    }
    @Override
    public void visit(BitwiseOr bitwiseOr) {
    }
    @Override
    public void visit(BitwiseXor bitwiseXor) {
    }
    @Override
    public void visit(CastExpression cast) {
    }

    @Override
    public void visit(TryCastExpression tryCastExpression) {

    }

    @Override
    public void visit(Modulo modulo) {
    }
    @Override
    public void visit(AnalyticExpression aexpr) {
    }
    @Override
    public void visit(ExtractExpression eexpr) {
    }
    @Override
    public void visit(IntervalExpression iexpr) {
    }
    @Override
    public void visit(OracleHierarchicalExpression oexpr) {
    }
    @Override
    public void visit(RegExpMatchOperator rexpr) {
    }
    @Override
    public void visit(JsonExpression jsonExpr) {
    }
    @Override
    public void visit(JsonOperator jsonExpr) {
    }
    @Override
    public void visit(RegExpMySQLOperator regExpMySQLOperator) {
    }
    @Override
    public void visit(UserVariable var) {
    }
    @Override
    public void visit(NumericBind bind) {
    }
    @Override
    public void visit(KeepExpression aexpr) {
    }
    @Override
    public void visit(MySQLGroupConcat groupConcat) {
    }
    @Override
    public void visit(ValueListExpression valueList) {
    }
    @Override
    public void visit(RowConstructor rowConstructor) {
    }

    @Override
    public void visit(RowGetExpression rowGetExpression) {

    }

    @Override
    public void visit(OracleHint hint) {
    }
    @Override
    public void visit(TimeKeyExpression timeKeyExpression) {
    }
    @Override
    public void visit(DateTimeLiteralExpression literal) {
    }
    @Override
    public void visit(NotExpression aThis) {
    }

    @Override
    public void visit(NextValExpression aThis) {
    }

    @Override
    public void visit(CollateExpression aThis) {
    }

    @Override
    public void visit(SimilarToExpression aThis) {
    }

    @Override
    public void visit(ArrayExpression arrayExpression) {
    }

    @Override
    public void visit(ArrayConstructor arrayConstructor) {

    }

    @Override
    public void visit(VariableAssignment variableAssignment) {
    }

    @Override
    public void visit(XMLSerializeExpr xmlSerializeExpr) {
    }

    @Override
    public void visit(TimezoneExpression timezoneExpression) {
    }
    @Override
    public void visit(JsonAggregateFunction jsonAggregateFunction) {
    }
    @Override
    public void visit(JsonFunction jsonFunction) {
    }
    @Override
    public void visit(ConnectByRootOperator connectByRootOperator) {
    }
    @Override
    public void visit(OracleNamedFunctionParameter oracleNamedFunctionParameter) {
    }

    @Override
    public void visit(AllColumns allColumns) {

    }

    @Override
    public void visit(AllTableColumns allTableColumns) {

    }

    @Override
    public void visit(AllValue allValue) {

    }

    @Override
    public void visit(IsDistinctExpression isDistinctExpression) {
    }

    @Override
    public void visit(GeometryDistance geometryDistance) {
    }

    @Override
    public void visit(BitwiseRightShift aThis) {
    }
    @Override
    public void visit(BitwiseLeftShift aThis) {
    }
    @Override
    public void visit(NullValue nullValue) {
    }
    @Override
    public void visit(Function function) {
    }
    @Override
    public void visit(SignedExpression signedExpression) {
    }
    @Override
    public void visit(JdbcParameter jdbcParameter) {
    }
    @Override
    public void visit(JdbcNamedParameter jdbcNamedParameter) {
    }
    @Override
    public void visit(DoubleValue doubleValue) {
    }
    @Override
    public void visit(LongValue longValue) {
    }
    @Override
    public void visit(HexValue hexValue) {
    }
    @Override
    public void visit(DateValue dateValue) {
    }
    @Override
    public void visit(TimeValue timeValue) {
    }
    @Override
    public void visit(TimestampValue timestampValue) {
    }
    @Override
    public void visit(Parenthesis parenthesis) {
    }
    @Override
    public void visit(StringValue stringValue) {
    }
    @Override
    public void visit(Addition addition) {
    }
    @Override
    public void visit(Division division) {
    }

    @Override
    public void visit(IntegerDivision integerDivision) {
    }
    @Override
    public void visit(Multiplication multiplication) {
    }
    @Override
    public void visit(Subtraction subtraction) {
    }
}
