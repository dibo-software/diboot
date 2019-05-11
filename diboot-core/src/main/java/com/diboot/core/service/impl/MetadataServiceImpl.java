package com.diboot.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.entity.Metadata;
import com.diboot.core.mapper.MetadataMapper;
import com.diboot.core.service.MetadataService;
import com.diboot.core.util.*;
import com.diboot.core.vo.KeyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 元数据相关service实现
 * @author Mazhicheng
 * @version 2.0
 * @date 2019/01/01
 */
@Service
public class MetadataServiceImpl extends BaseServiceImpl<MetadataMapper, Metadata> implements MetadataService{
    private static final Logger log = LoggerFactory.getLogger(MetadataServiceImpl.class);

    private static final String FIELD_NAME_ITEM_NAME = BeanUtils.convertToFieldName(Metadata::getItemName);
    private static final String FIELD_NAME_ITEM_VALUE = BeanUtils.convertToFieldName(Metadata::getItemValue);
    private static final String FIELD_NAME_TYPE = BeanUtils.convertToFieldName(Metadata::getType);
    private static final String FIELD_NAME_PARENT_ID = BeanUtils.convertToFieldName(Metadata::getParentId);

    @Override
    public List<KeyValue> getKeyValueList(String type) {
        // 构建查询条件
        Wrapper queryMetadata = new QueryWrapper<Metadata>().lambda()
                .select(Metadata::getItemName, Metadata::getItemValue)
                .eq(Metadata::getType, type)
                .gt(Metadata::getParentId, 0);
        // 返回构建条件
        return getKeyValueList(queryMetadata);
    }

    @Override
    public <T1,T2,S> void bindItemLabel(List voList, ISetter<T1,S> setFieldLabelFn,
                               IGetter<T2> getFieldIdFn, String type){
        if(V.isEmpty(voList)){
            return;
        }
        bindingFieldTo(voList)
            .link(Metadata::getItemName, setFieldLabelFn)
            .joinOn(getFieldIdFn, Metadata::getItemValue)
            .andEQ(FIELD_NAME_TYPE, type)
            .andGT(FIELD_NAME_PARENT_ID, 0)
            .bind();
    }

    @Override
    public void bindItemLabel(List voList, String setFieldName, String getFieldName, String type){
        if(V.isEmpty(voList)){
            return;
        }
        getFieldName = S.toLowerCaseCamel(getFieldName);
        bindingFieldTo(voList)
                .link(FIELD_NAME_ITEM_NAME, setFieldName)
                .joinOn(getFieldName, FIELD_NAME_ITEM_VALUE)
                .andEQ(FIELD_NAME_TYPE, type)
                .andGT(FIELD_NAME_PARENT_ID, 0)
                .bind();
    }
}
