package com.diboot.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.entity.Dictionary;
import com.diboot.core.mapper.DictionaryMapper;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.example.service.DictionaryService;
import com.diboot.example.vo.DictionaryVO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 数据字典相关Service
 * @author Wangyongliang
 * @version v2.0
 * @date 2019/7/8
 */
@Service("dictionaryService")
@Slf4j
public class DictionaryServiceImpl extends BaseServiceImpl<DictionaryMapper, Dictionary> implements DictionaryService {
    private static final Logger logger = LoggerFactory.getLogger(DictionaryServiceImpl.class);

    @Override
    @Transactional
    public boolean createDictionary(DictionaryVO entityVO) {
        //将DictionaryVO转化为Dictionary
        Dictionary dictionary = new Dictionary();
        dictionary = (Dictionary)BeanUtils.copyProperties(entityVO, dictionary);
        if(!super.createEntity(dictionary)){
            logger.warn("新建父数据字典失败，type="+entityVO.getType());
            return false;
        }
        List<Dictionary> children = entityVO.getChildren();
        if(V.notEmpty(children)){
            try {
                for(Dictionary dict : children){
                    dict.setParentId(dictionary.getId());
                    dict.setType(dictionary.getType());
                }
                if(!super.createEntities(children)){
                    logger.warn("新建子数据字典失败，type="+entityVO.getType());
                    throw new RuntimeException();
                }
            } catch (Exception e) {
                logger.warn("新建子数据字典失败，type="+entityVO.getType());
                throw new RuntimeException();
            }
        }

        return true;
    }

    @Override
    @Transactional
    public boolean updateDictionary(DictionaryVO entityVO) {
        //将DictionaryVO转化为Dictionary
        Dictionary dictionary = new Dictionary();
        dictionary = (Dictionary)BeanUtils.copyProperties(entityVO, dictionary);
        if(!super.updateEntity(dictionary)){
            logger.warn("更新父数据字典失败，type="+entityVO.getType());
            return false;
        }
        //获取原 子数据字典list
        QueryWrapper<Dictionary> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(Dictionary::getParentId, entityVO.getId());
        List<Dictionary> oldDictList = super.getEntityList(queryWrapper);
        List<Dictionary> newDictList = entityVO.getChildren();
        StringBuffer newBuffer = new StringBuffer();

        if(V.notEmpty(newDictList)){
            for(Dictionary dict : newDictList){
                dict.setType(entityVO.getType());
                dict.setParentId(entityVO.getId());
                if(V.notEmpty(dict.getId())){
                    newBuffer.append(dict.getId()).append(",");
                    if(!super.updateEntity(dict)){
                        logger.warn("更新子数据字典失败，itemName="+dict.getItemName());
                        throw new RuntimeException();
                    }
                }else{
                    if(!super.createEntity(dict)){
                        logger.warn("新建子数据字典失败，itemName="+dict.getItemName());
                        throw new RuntimeException();
                    }
                }
            }
        }

        if(V.notEmpty(oldDictList)){
            for(Dictionary dict : oldDictList){
                if(!(newBuffer.toString().contains(dict.getId().toString()))){
                    if(!super.deleteEntity(dict.getId())){
                        logger.warn("删除子数据字典失败，itemName="+dict.getItemName());
                        throw new RuntimeException();
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean deleteDictionary(Long id) {
        QueryWrapper<Dictionary> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(Dictionary::getId, id)
                             .or()
                             .eq(Dictionary::getParentId, id);
        try {
            super.deleteEntities(queryWrapper);
        } catch (Exception e) {
            logger.warn("删除数据字典失败", e);
            return false;
        }
        return true;
    }
}
