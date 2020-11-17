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
package com.diboot.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.config.Cons;
import com.diboot.core.entity.Dictionary;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.mapper.DictionaryMapper;
import com.diboot.core.service.BindDictService;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.V;
import com.diboot.core.vo.DictionaryVO;
import com.diboot.core.vo.KeyValue;
import com.diboot.core.vo.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据字典相关service实现
 * @author mazc@dibo.ltd
 * @version 2.0
 * @date 2019/01/01
 */
@Primary
@Service("dictionaryService")
public class DictionaryServiceImpl extends BaseServiceImpl<DictionaryMapper, Dictionary> implements DictionaryService, BindDictService {
    private static final Logger log = LoggerFactory.getLogger(DictionaryServiceImpl.class);

    @Override
    public List<KeyValue> getKeyValueList(String type) {
        // 构建查询条件
        Wrapper queryDictionary = new QueryWrapper<Dictionary>().lambda()
                .select(Dictionary::getItemName, Dictionary::getItemValue)
                .eq(Dictionary::getType, type)
                .gt(Dictionary::getParentId, 0)
                .orderByAsc(Dictionary::getSortId, Dictionary::getId);
        // 返回构建条件
        return getKeyValueList(queryDictionary);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createDictAndChildren(DictionaryVO dictVO) {
        Dictionary dictionary = dictVO;
        if(!super.createEntity(dictionary)){
            log.warn("新建数据字典定义失败，type="+dictVO.getType());
            return false;
        }
        List<Dictionary> children = dictVO.getChildren();
        if(V.notEmpty(children)){
            for(Dictionary dict : children){
                dict.setParentId(dictionary.getId());
                dict.setType(dictionary.getType());
            }
            // 批量保存
            boolean success = super.createEntities(children);
            if(!success){
                String errorMsg = "新建数据字典子项失败，type="+dictVO.getType();
                log.warn(errorMsg);
                throw new BusinessException(Status.FAIL_OPERATION, errorMsg);
            }
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDictAndChildren(DictionaryVO dictVO) {
        //将DictionaryVO转化为Dictionary
        Dictionary dictionary = dictVO;
        if(!super.updateEntity(dictionary)){
            log.warn("更新数据字典定义失败，type="+dictVO.getType());
            return false;
        }
        //获取原 子数据字典list
        QueryWrapper<Dictionary> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(Dictionary::getParentId, dictVO.getId());
        List<Dictionary> oldDictList = super.getEntityList(queryWrapper);
        List<Dictionary> newDictList = dictVO.getChildren();
        Set<Long> dictItemIds = new HashSet<>();
        if(V.notEmpty(newDictList)){
            for(Dictionary dict : newDictList){
                dict.setType(dictVO.getType()).setParentId(dictVO.getId());
                if(V.notEmpty(dict.getId())){
                    dictItemIds.add(dict.getId());
                    if(!super.updateEntity(dict)){
                        log.warn("更新字典子项失败，itemName=" + dict.getItemName());
                        throw new BusinessException(Status.FAIL_EXCEPTION, "更新字典子项异常");
                    }
                }
                else{
                    if(!super.createEntity(dict)){
                        log.warn("新建字典子项失败，itemName=" + dict.getItemName());
                        throw new BusinessException(Status.FAIL_EXCEPTION, "新建字典子项异常");
                    }
                }
            }
        }
        if(V.notEmpty(oldDictList)){
            for(Dictionary dict : oldDictList){
                if(!dictItemIds.contains(dict.getId())){
                    if(!super.deleteEntity(dict.getId())){
                        log.warn("删除子数据字典失败，itemName="+dict.getItemName());
                        throw new BusinessException(Status.FAIL_EXCEPTION, "删除字典子项异常");
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean deleteDictAndChildren(Long id) {
        QueryWrapper<Dictionary> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .eq(Dictionary::getId, id)
                .or()
                .eq(Dictionary::getParentId, id);
        deleteEntities(queryWrapper);
        return true;
    }


    @Override
    public void sortList(List<Dictionary> dictionaryList) {
        if (V.isEmpty(dictionaryList)) {
            throw new BusinessException(Status.FAIL_OPERATION, "排序列表不能为空");
        }
        List<Long> sortIdList = new ArrayList();
        // 先将所有序号重新设置为自身当前id
        for (Dictionary item : dictionaryList) {
            item.setSortId(item.getId());
            sortIdList.add(item.getSortId());
        }
        // 将序号列表倒序排序
        sortIdList = sortIdList.stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        // 整理需要更新的列表
        List<Dictionary> updateList = new ArrayList<>();
        for (int i=0; i<dictionaryList.size(); i++) {
            Dictionary item = dictionaryList.get(i);
            Dictionary updateItem = new Dictionary();
            updateItem.setId(item.getId());
            updateItem.setSortId(sortIdList.get(i));
            updateList.add(updateItem);
        }
        if (updateList.size() > 0) {
            super.updateBatchById(updateList);
        }
    }

    @Override
    public void bindItemLabel(List voList, String setFieldName, String getFieldName, String type){
        if(V.isEmpty(voList)){
            return;
        }
        bindingFieldTo(voList)
                .link(Cons.FIELD_ITEM_NAME, setFieldName)
                .joinOn(getFieldName, Cons.FIELD_ITEM_VALUE)
                .andEQ(Cons.FIELD_TYPE, type)
                .andGT(Cons.FieldName.parentId.name(), 0)
                .bind();
    }

}