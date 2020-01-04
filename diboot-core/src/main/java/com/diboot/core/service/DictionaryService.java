package com.diboot.core.service;

import com.diboot.core.entity.Dictionary;
import com.diboot.core.util.IGetter;
import com.diboot.core.util.ISetter;
import com.diboot.core.vo.DictionaryVO;
import com.diboot.core.vo.KeyValue;

import java.util.List;

/**
 * 数据字典Service
 * @author Mazhicheng
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
     * 添加多层级数据字典
     * @param dictVO
     * @return
     */
    boolean addDictTree(DictionaryVO dictVO);

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
