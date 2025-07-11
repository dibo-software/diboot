/*
 * Copyright (c) 2015-2099, www.dibo.ltd (service@dibo.ltd).
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

import com.diboot.core.exception.InvalidUsageException;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.conditional.XorExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;

import java.util.ArrayList;
import java.util.List;

/**
 * 关联注解条件解析器
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/3/30
 */
public class ConditionParser extends ExpressionVisitorAdapter<Void> {

    private List<Expression> expressList = new ArrayList<>(4);

    public ConditionParser() {
        this.expressList.clear();
    }

    /**
     * 添加错误信息
     * @param errorMsg
     */
    private void throwError(String errorMsg) {
        throw new InvalidUsageException(errorMsg);
    }

    /**
     * 获取解析后的结果
     * @return
     * @throws Exception
     */
    public List<Expression> getExpressList() {
        return expressList;
    }

    @Override
    public Void visit(AndExpression andExpression, Object o) {
        andExpression.getLeftExpression().accept(this);
        andExpression.getRightExpression().accept(this);
        return null;
    }

    // ----- 支持的条件
    @Override
    public Void visit(EqualsTo equalsTo, Object o) {
        if(!(equalsTo.getLeftExpression() instanceof Column)){
            throwError("= 条件左侧必须为字段/列名");
        }
        expressList.add(equalsTo);
        return null;
    }

    @Override
    public Void visit(GreaterThan greaterThan, Object o) {
        if(!(greaterThan.getLeftExpression() instanceof Column)){
            throwError("> 条件左侧必须为字段/列名");
        }
        expressList.add(greaterThan);
        return null;
    }

    @Override
    public Void visit(NotEqualsTo notEqualsTo, Object o) {
        if(!(notEqualsTo.getLeftExpression() instanceof Column)){
            throwError("!= 条件左侧必须为字段/列名");
        }
        expressList.add(notEqualsTo);
        return null;
    }

    @Override
    public Void visit(GreaterThanEquals greaterThanEquals, Object o) {
        if(!(greaterThanEquals.getLeftExpression() instanceof Column)){
            throwError(">= 条件左侧必须为字段/列名");
        }
        expressList.add(greaterThanEquals);
        return null;
    }

    @Override
    public Void visit(InExpression inExpression, Object o) {
        if(!(inExpression.getLeftExpression() instanceof Column)){
            throwError("IN 条件左侧必须为字段/列名");
        }
        expressList.add(inExpression);
        return null;
    }

    @Override
    public Void visit(MinorThan minorThan, Object o) {
        if(!(minorThan.getLeftExpression() instanceof Column)){
            throwError("< 条件左侧必须为字段/列名");
        }
        expressList.add(minorThan);
        return null;
    }

    @Override
    public Void visit(MinorThanEquals minorThanEquals, Object o) {
        if(!(minorThanEquals.getLeftExpression() instanceof Column)){
            throwError("<= 条件左侧必须为字段/列名");
        }
        expressList.add(minorThanEquals);
        return null;
    }

    @Override
    public Void visit(IsNullExpression isNullExpression, Object o) {
        if(!(isNullExpression.getLeftExpression() instanceof Column)){
            throwError("Is Null 条件左侧必须为字段/列名");
        }
        expressList.add(isNullExpression);
        return null;
    }

    @Override
    public Void visit(LikeExpression likeExpression, Object o) {
        if(!(likeExpression.getLeftExpression() instanceof Column)){
            throwError("Like 条件左侧必须为字段/列名");
        }
        expressList.add(likeExpression);
        return null;
    }

    @Override
    public Void visit(Between between, Object o) {
        if(!(between.getLeftExpression() instanceof Column)){
            throwError("Between 条件左侧必须为字段/列名");
        }
        expressList.add(between);
        return null;
    }

    //------- 暂不支持的条件
    @Override
    public Void visit(OrExpression orExpression, Object o) {
        throwError("暂不支持 OR 连接条件: " + orExpression.toString());
        return null;
    }

    @Override
    public Void visit(XorExpression xorExpression, Object o) {
        throwError("暂不支持 XOR 关联条件: " + xorExpression.toString());
        return null;
    }

}
