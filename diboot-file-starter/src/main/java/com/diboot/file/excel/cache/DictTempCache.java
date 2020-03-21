package com.diboot.file.excel.cache;

import com.diboot.core.binding.annotation.BindDict;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.V;
import com.diboot.core.vo.KeyValue;
import com.diboot.file.excel.BaseExcelModel;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据字典临时缓存
 * @author Mazc@dibo.ltd
 * @version v2.0
 * @date 2020/02/22
 */
@Slf4j
public class DictTempCache {
    /**
     * 数据字典-子项映射的缓存
     */
    private static Map<String, Map<String, Object>> DICT_TYPE_ITEMS_MAP = new ConcurrentHashMap<>();
    /**
     * 数据字典-缓存时间戳缓存
     */
    private static Map<String, Long> DICT_TYPE_TIMESTAMP_MAP = new ConcurrentHashMap<>();
    /**
     * 无字典绑定的缓存
     */
    private static Set<String> NO_DICT_MODELS = new HashSet<>();
    /**
     * model-字典映射
     */
    private static Map<String, List<String>> MODEL_DICTS = new HashMap<>();

    /**
     * 刷新model字典缓存
     * @param modelClass
     * @param <T>
     */
    public static <T extends BaseExcelModel> void refreshDictCache(Class<T> modelClass){
        // 无字典model
        if(NO_DICT_MODELS.contains(modelClass.getName())){
            return;
        }
        List<String> dictTypes = extractDictTypes(modelClass);
        if(V.notEmpty(dictTypes)){
            for(String dictType : dictTypes){
                if(DICT_TYPE_ITEMS_MAP.containsKey(dictType) == false || isExpired(DICT_TYPE_TIMESTAMP_MAP.get(dictType)) == true){
                    List<KeyValue> list = ContextHelper.getBean(DictionaryService.class).getKeyValueList(dictType);
                    Map<String, Object> name2ValueMap = BeanUtils.convertKeyValueList2Map(list);
                    DICT_TYPE_ITEMS_MAP.put(dictType, name2ValueMap);
                    DICT_TYPE_TIMESTAMP_MAP.put(dictType, System.currentTimeMillis());
                }
            }
        }
    }

    /**
     * 获取字典值
     * @param dictType
     * @param dictName
     * @return
     */
    public static String getDictValue(String dictType, String dictName){
        Map<String, Object> map = DICT_TYPE_ITEMS_MAP.get(dictType);
        if(map == null){
            log.warn("无法找到数据字典定义: "+dictType);
            return dictName;
        }
        if(map.get(dictName) == null){
            return null;
        }
        return (String)map.get(dictName);
    }

    /**
     * 获取字典名label
     * @param dictType
     * @param dictValue
     * @return
     */
    public static String getDictLabel(String dictType, String dictValue) throws Exception{
        Map<String, Object> map = DICT_TYPE_ITEMS_MAP.get(dictType);
        if(map == null){
            log.warn("无法找到数据字典定义: "+dictType);
            return dictValue;
        }
        for( Map.Entry<String, Object> entry : map.entrySet()){
            if(dictValue.equals(entry.getValue())){
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * 提取注解绑定
     * @param modelClass
     * @param <T>
     * @return
     */
    private static <T extends BaseExcelModel> List<String> extractDictTypes(Class<T> modelClass){
        if(MODEL_DICTS.containsKey(modelClass.getName())){
            return MODEL_DICTS.get(modelClass.getName());
        }
        // 检测model是否包含dict注解
        List<String> dictTypes = new ArrayList<>();
        BeanUtils.extractAllFields(modelClass).forEach(fld -> {
            if(fld.getAnnotation(BindDict.class) != null){
                BindDict bindDict = fld.getAnnotation(BindDict.class);
                dictTypes.add(bindDict.type());
            }
        });
        if(dictTypes.isEmpty()){
            NO_DICT_MODELS.add(modelClass.getName());
        }
        else{
            MODEL_DICTS.put(modelClass.getName(), dictTypes);
        }
        return dictTypes;
    }

    /**
     * 是否超期
     * @return
     */
    private static boolean isExpired(Long cacheTime){
        if(cacheTime == null){
            return true;
        }
        return (System.currentTimeMillis() - cacheTime) > 600000;
    }

}
