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
import com.diboot.core.util.IGetter;
import com.diboot.core.util.ISetter;
import com.diboot.core.vo.DictionaryVO;
import com.diboot.core.vo.KeyValue;

import java.util.List;

/**
 * 数据字典Service
 * @author mazc@dibo.ltd
 * @version 2.0
 * @date 2019/01/01
 */
public interface DictionaryService extends BaseService<Dictionary>{

    /***
     * 获取对应类型的键值对
     * @param type
     * @return
     */
    List<KeyValue> getKeyValueList(String type);

    /***
     * 绑定itemName字段到VoList
     * @param voList
     * @param setFieldLabelFn
     * @param getFieldIdFn
     * @param <T1>
     * @param <T2>
     * @param <S>
     */
    <T1,T2,S> void bindItemLabel(List voList, ISetter<T1, S> setFieldLabelFn,
                                 IGetter<T2> getFieldIdFn, String type);

    /***
     * 绑定itemName字段到VoList
     * @param voList
     * @param setFieldName
     * @param getFieldName
     */
    void bindItemLabel(List voList, String setFieldName, String getFieldName, String type);

    /***
     * 添加多层级数据字典 (已废弃，请调用createDictAndChildren)
     * @param dictVO
     * @return
     */
    @Deprecated
    boolean addDictTree(DictionaryVO dictVO);

    /**
     * 添加字典定义及其子项
     * @param dictVO
     * @return
     */
    boolean createDictAndChildren(DictionaryVO dictVO);

    /**
     * 更新字典定义及其子项
     * @param dictVO
     * @return
     */
    boolean updateDictAndChildren(DictionaryVO dictVO);

    /**
     * 删除字典定义及其子项
     * @param id
     * @return
     */
    boolean deleteDictAndChildren(Long id);

}
