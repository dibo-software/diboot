package com.diboot.core.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.binding.parser.ParserCache;
import com.diboot.core.config.BaseConfig;
import com.diboot.core.dto.AttachMoreDTO;
import com.diboot.core.entity.Dictionary;
import com.diboot.core.entity.ValidList;
import com.diboot.core.service.BaseService;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.KeyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取附加属性的通用接口
 *
 * @author : uu
 * @version : v2.0
 * @Date 2020/6/11  16:02
 */
@RestController
@RequestMapping("/attachMore")
public class AttachMoreController {

    @Autowired
    private DictionaryService dictionaryService;

    private final static Logger log = LoggerFactory.getLogger(AttachMoreController.class);

    /**
     * 获取kvList的通用接口
     *
     * @param attachMoreDTOList
     * @return
     */
    @PostMapping
    public JsonResult attachMore(@Valid @RequestBody ValidList<AttachMoreDTO> attachMoreDTOList) {
        Map<String, Object> result = new HashMap<>(16);
        String kvLimitCountStr = BaseConfig.getProperty("diboot.core.kv-limit-count", "100");
        Integer kvLimitCount = 100;
        try {
            kvLimitCount = Integer.valueOf(kvLimitCountStr);
        } catch (Exception e) {
            log.warn("diboot.core.kvLimitCount配置只能为整数，当前使用默认配置长度【100】", e);
        }
        for (AttachMoreDTO attachMoreDTO : attachMoreDTOList) {
            AttachMoreDTO.REF_TYPE type = attachMoreDTO.getType();
            if (type.equals(AttachMoreDTO.REF_TYPE.D)) {
                List<KeyValue> keyValueList = dictionaryService.getKeyValueList(
                        Wrappers.query()
                                .select(S.toSnakeCase(BeanUtils.convertToFieldName(Dictionary::getItemName)), S.toSnakeCase(BeanUtils.convertToFieldName(Dictionary::getItemValue)))
                                .eq(S.toSnakeCase(BeanUtils.convertToFieldName(Dictionary::getType)), attachMoreDTO.getTarget())
                                .gt(S.toSnakeCase(BeanUtils.convertToFieldName(Dictionary::getParentId)), 0)
                                .last("limit " + kvLimitCount)
                );
                result.put(S.toLowerCaseCamel(attachMoreDTO.getTarget()) + "KvList", keyValueList);
            } else if (type.equals(AttachMoreDTO.REF_TYPE.T)) {
                String entityName = attachMoreDTO.getTarget();
                String entityClassName = S.capFirst(entityName);
                Class<?> entityClass = ParserCache.getEntityClassByEntityLowerCaseCamel(entityClassName);
                if (V.isEmpty(entityClass)) {
                    log.warn("传递错误的实体类型：{}", attachMoreDTO.getTarget());
                    continue;
                }
                String value = V.isEmpty(attachMoreDTO.getValue()) ? ContextHelper.getPrimaryKey(entityClass) : attachMoreDTO.getValue();
                String key = attachMoreDTO.getKey();
                if (V.isEmpty(key)) {
                    for (Field field : entityClass.getDeclaredFields()) {
                        if (V.equals(field.getType().getName(), String.class.getName())) {
                            key = field.getName();
                            break;
                        }
                    }
                }
                BaseService baseService = ContextHelper.getBaseServiceByEntity(entityClass);
                List<KeyValue> keyValueList = baseService.getKeyValueList(
                        Wrappers.query()
                                .select(key, value)
                                .last("limit " + kvLimitCount)
                );
                result.put(S.uncapFirst(entityName) + "KvList", keyValueList);
            } else {
                log.error("错误的加载绑定类型：{}", attachMoreDTO.getType());
            }
        }
        return JsonResult.OK(result);
    }
}
