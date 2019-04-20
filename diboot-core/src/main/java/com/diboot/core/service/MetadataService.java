package com.diboot.core.service;

import com.diboot.core.entity.Metadata;
import com.diboot.core.util.IGetter;
import com.diboot.core.util.ISetter;
import com.diboot.core.vo.KeyValue;

import java.util.List;

/**
 * 元数据Service
 * @author Mazhicheng
 * @version 2.0
 * @date 2019/01/01
 */
public interface MetadataService extends BaseService<Metadata>{

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
}
