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
package com.diboot.core.config;

/**
 * 基础常量定义
 * @author mazc@dibo.ltd
 * @version 2.0
 * @date 2019/01/01
 */
public class Cons {
    /**
     * 默认字符集UTF-8
     */
    public static final String CHARSET_UTF8 = "UTF-8";
    /**
     * 逗号分隔符 ,
     */
    public static final String SEPARATOR_COMMA = ",";
    /**
     * 下划线分隔符_
     */
    public static final String SEPARATOR_UNDERSCORE = "_";
    /**
     * 横杠分隔符 -
     */
    public static final String SEPARATOR_CROSSBAR = "-";
    /**
     * 冒号分隔符
     */
    public final static  String SEPARATOR_COLON = ":";
    /**
     * 斜杠路径分隔符
     */
    public final static  String SEPARATOR_SLASH = "/";
    /**
     * 竖线分隔符，or
     */
    public final static  String SEPARATOR_OR = "|";
    /**
     * 分号分隔符
     */
    public final static  String SEPARATOR_SEMICOLON = ";";
    /**
     * 点分隔符
     */
    public static final String SEPARATOR_DOT = ".";
    /**
     * 排序 - 降序标记
     */
    public static final String ORDER_DESC = "DESC";
    /**
     * 逻辑删除列名
     */
    public static final String COLUMN_IS_DELETED = "is_deleted";
    /**
     * 创建时间列名
     */
    public static final String COLUMN_CREATE_TIME = "create_time";
    /***
     * 常用字段名定义
     */
    public enum FieldName{
        /**
         * 主键属性名
         */
        id,
        /**
         * 租户ID
         */
        tenantId,
        /**
         * 默认的上级ID属性名
         */
        parentId,
        /**
         * 子节点属性名
         */
        children,
        /**
         * 逻辑删除标记字段
         */
        deleted,
        /**
         * 创建时间字段
         */
        createTime,
        /**
         * 更新时间
         */
        updateTime,
        /**
         * 创建人
         */
        createBy,
        /**
         * 更新人
         */
        updateBy,
        /**
         * 组织id
         */
        orgId,
        /**
         * 用户id
         */
        userId
    }

    /***
     * 常用列名定义
     */
    public enum ColumnName{
        /**
         * 主键属性名
         */
        id,
        /**
         * 租户ID
         */
        tenant_id,
        /**
         * 默认的上级ID属性名
         */
        parent_id,
        /**
         * 子节点属性名
         */
        children,
        /**
         * 逻辑删除标记字段
         */
        is_deleted,
        /**
         * 创建时间字段
         */
        create_time,
        /**
         * 更新时间
         */
        update_time,
        /**
         * 创建人
         */
        create_by,
        /**
         * 更新人
         */
        update_by,
        /**
         * 组织id
         */
        org_id,
        /**
         * 用户id
         */
        user_id
    }

    /**
     * 分页相关参数
     */
    public enum PaginationParam {
        /**
         * 查询中排序参数名
         */
        orderBy,
        /**
         * 当前页数参数名
         */
        pageIndex,
        /**
         * 每页记录数参数名
         */
        pageSize,
        /**
         * 总数
         */
        totalCount;

        public static boolean isPaginationParam(String param) {
            return orderBy.name().equals(param)
                    || pageIndex.name().equals(param)
                    || pageSize.name().equals(param)
                    || totalCount.name().equals(param);
        }
    }

    /**
     * 字典Entity相关属性名定义
     */
    public static final String FIELD_ITEM_NAME = "itemName";
    public static final String FIELD_ITEM_VALUE = "itemValue";
    public static final String COLUMN_ITEM_VALUE = "item_value";
    public static final String FIELD_TYPE = "type";

    /**
     * token前缀
     */
    public static final String TOKEN_PREFIX_BEARER = "Bearer";
    /**
     * token header头名称
     */
    public static final String TOKEN_HEADER_NAME = "Authorization";

    /**
     * 启用/停用 状态字典定义
     */
    public enum ENABLE_STATUS{
        /**
         * 正常
         */
        A("正常"),
        /**
         * 停用
         */
        I("停用");

        private String label;
        ENABLE_STATUS(String label){
            this.label = label;
        }

        public String label(){
            return label;
        }
        public static String getLabel(String val){
            if(val.equalsIgnoreCase(A.name())){
                return A.label;
            }
            return I.label;
        }
    }

    /**
     * 成功/失败 结果状态字典定义
     */
    public enum RESULT_STATUS{
        /**
         * 正常
         */
        S("成功"),
        /**
         * 停用
         */
        F("失败");

        private String label;
        RESULT_STATUS(String label){
            this.label = label;
        }

        public String label(){
            return label;
        }
        public static String getLabel(String val){
            if(val.equalsIgnoreCase(S.name())){
                return S.label;
            }
            return F.label;
        }
    }

}
