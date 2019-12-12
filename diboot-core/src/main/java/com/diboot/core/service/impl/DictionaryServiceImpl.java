package com.diboot.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.entity.Dictionary;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.mapper.DictionaryMapper;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.IGetter;
import com.diboot.core.util.ISetter;
import com.diboot.core.util.V;
import com.diboot.core.vo.DictionaryVO;
import com.diboot.core.vo.KeyValue;
import com.diboot.core.vo.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 数据字典相关service实现
 * @author Mazhicheng
 * @version 2.0
 * @date 2019/01/01
 */
@Primary
@Service("dictionaryService")
public class DictionaryServiceImpl extends BaseServiceImpl<DictionaryMapper, Dictionary> implements DictionaryService {
    private static final Logger log = LoggerFactory.getLogger(DictionaryServiceImpl.class);

    private static final String FIELD_NAME_ITEM_NAME = BeanUtils.convertToFieldName(Dictionary::getItemName);
    private static final String FIELD_NAME_ITEM_VALUE = BeanUtils.convertToFieldName(Dictionary::getItemValue);
    private static final String FIELD_NAME_TYPE = BeanUtils.convertToFieldName(Dictionary::getType);
    private static final String FIELD_NAME_PARENT_ID = BeanUtils.convertToFieldName(Dictionary::getParentId);

    @Override
    public List<KeyValue> getKeyValueList(String type) {
        // 构建查询条件
        Wrapper queryDictionary = new QueryWrapper<Dictionary>().lambda()
                .select(Dictionary::getItemName, Dictionary::getItemValue)
                .eq(Dictionary::getType, type)
                .gt(Dictionary::getParentId, 0);
        // 返回构建条件
        return getKeyValueList(queryDictionary);
    }

    @Override
    public <T1,T2,S> void bindItemLabel(List voList, ISetter<T1,S> setFieldLabelFn,
                               IGetter<T2> getFieldIdFn, String type){
        if(V.isEmpty(voList)){
            return;
        }
        bindingFieldTo(voList)
            .link(Dictionary::getItemName, setFieldLabelFn)
            .joinOn(getFieldIdFn, Dictionary::getItemValue)
            .andEQ(FIELD_NAME_TYPE, type)
            .andGT(FIELD_NAME_PARENT_ID, 0)
            .bind();
    }

    @Override
    public void bindItemLabel(List voList, String setFieldName, String getFieldName, String type){
        if(V.isEmpty(voList)){
            return;
        }
        bindingFieldTo(voList)
                .link(FIELD_NAME_ITEM_NAME, setFieldName)
                .joinOn(getFieldName, FIELD_NAME_ITEM_VALUE)
                .andEQ(FIELD_NAME_TYPE, type)
                .andGT(FIELD_NAME_PARENT_ID, 0)
                .bind();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addDictTree(DictionaryVO dictVO) {
        //将DictionaryVO转化为Dictionary
        Dictionary dictionary = new Dictionary();
        dictionary = (Dictionary) BeanUtils.copyProperties(dictVO, dictionary);
        if(!super.createEntity(dictionary)){
            log.warn("新建数据字典定义失败，type="+dictVO.getType());
            return false;
        }
        List<Dictionary> children = dictVO.getChildren();
        if(V.notEmpty(children)){
            boolean success = true;
            for(Dictionary dict : children){
                dict.setParentId(dictionary.getId());
                dict.setType(dictionary.getType());
                boolean insertOK = super.createEntity(dict);
                if (!insertOK){
                    log.warn("dictionary插入数据字典失败，请检查！");
                    success = false;
                }
            }
            if(!success){
                String errorMsg = "新建数据字典子项失败，type="+dictVO.getType();
                log.warn(errorMsg);
                throw new BusinessException(Status.FAIL_OPERATION, errorMsg);
            }
        }
        return true;
    }
}