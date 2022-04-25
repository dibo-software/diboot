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
package com.diboot.iam.annotation;

/**
 * 权限-操作 类型定义
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/12/23
 */
public class OperationCons {
    /**
     * 操作权限类型 - 读数据
     */
    public static final String CODE_READ = "read";
    /**
     * 操作权限类型 - 写数据
     */
    public static final String CODE_WRITE = "write";

    /**
     * 操作权限类型 - 首页
     */
    public static final String CODE_INDEX = "index";
    /**
     * 操作权限类型 - 查看列表
     */
    public static final String CODE_LIST = "list";
    /**
     * 操作权限类型 - 查看详情
     */
    public static final String CODE_DETAIL = "detail";
    /**
     * 操作权限类型 - 添加
     */
    public static final String CODE_CREATE = "create";
    /**
     * 操作权限类型 - 更新
     */
    public static final String CODE_UPDATE = "update";
    /**
     * 操作权限类型 - 删除
     */
    public static final String CODE_DELETE = "delete";
    /**
     * 操作权限类型 - 撤销删除
     */
    public static final String CODE_CANCEL_DELETE = "cancelDelete";
    /**
     * 操作权限类型 - 批量删除
     */
    public static final String CODE_BATCH_DELETE = "batchDelete";
    /**
     * 操作权限类型 - 批量更新
     */
    public static final String CODE_BATCH_UPDATE = "batchUpdate";
    /**
     * 操作权限类型 - 导入
     */
    public static final String CODE_IMPORT = "import";
    /**
     * 操作权限类型 - 导出
     */
    public static final String CODE_EXPORT = "export";

    /**
     * 操作权限类型描述 - 读数据
     */
    public static final String LABEL_READ = "读权限";
    /**
     * 操作权限类型描述 - 写数据
     */
    public static final String LABEL_WRITE = "写权限";

    /**
     * 操作权限类型描述 - 查看首页
     */
    public static final String LABEL_INDEX = "查看首页";
    /**
     * 操作权限描述 - 查看列表
     */
    public static final String LABEL_LIST = "查看列表";
    /**
     * 操作权限描述 - 查看详情
     */
    public static final String LABEL_DETAIL = "查看详情";
    /**
     * 操作权限描述 - 添加
     */
    public static final String LABEL_CREATE = "创建";
    /**
     * 操作权限描述 - 更新
     */
    public static final String LABEL_UPDATE = "更新";
    /**
     * 操作权限描述 - 删除
     */
    public static final String LABEL_DELETE = "删除";
    /**
     * 操作权限描述 - 撤销删除
     */
    public static final String LABEL_CANCEL_DELETE = "撤销删除";
    /**
     * 操作权限描述 - 批量删除
     */
    public static final String LABEL_BATCH_DELETE = "批量删除";
    /**
     * 操作权限描述 - 批量更新
     */
    public static final String LABEL_BATCH_UPDATE = "批量更新";
    /**
     * 操作权限描述 - 导入
     */
    public static final String LABEL_IMPORT = "导入";
    /**
     * 操作权限描述 - 导出
     */
    public static final String LABEL_EXPORT = "导出";
}
