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
package com.diboot.core.service;

import com.diboot.core.entity.Dictionary;
import com.diboot.core.vo.DictionaryVO;
import com.diboot.core.vo.LabelValue;

import java.util.List;

/**
 * BindDict等字典服务绑定Service提供接口
 *
 * @author mazc@dibo.ltd
 * @version 2.2.0
 * @date 2020/11/17
 */
public interface DictionaryServiceExtProvider {

    /**
     * 绑定字典的label
     *
     * @param voList
     * @param setFieldName
     * @param getFieldName
     * @param type
     */
    void bindItemLabel(List voList, String setFieldName, String getFieldName, String type);

    /**
     * 获取字典类型对应的子项键值对
     *
     * @param dictType
     * @return
     */
    List<LabelValue> getLabelValueList(String dictType);

    /**
     * 是否存在某字典类型定义
     *
     * @param dictType
     * @return
     */
    boolean existsDictType(String dictType);

    /**
     * 创建字典及子项
     *
     * @param dictionaryVO
     * @return
     */
    boolean createDictAndChildren(DictionaryVO dictionaryVO);

    /**
     * 查询字典定义的List（不含子项）
     *
     * @return
     */
    List<Dictionary> getDictDefinitionList();

    /**
     * 查询字典VOList（含子项）
     *
     * @return
     */
    List<DictionaryVO> getDictDefinitionVOList();

}
