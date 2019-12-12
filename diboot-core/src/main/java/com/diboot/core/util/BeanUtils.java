package com.diboot.core.util;


import com.diboot.core.config.Cons;
import com.diboot.core.entity.BaseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.core.ResolvableType;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bean相关处理工具类
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/01/01
 */
public class BeanUtils {
    private static final Logger log = LoggerFactory.getLogger(BeanUtils.class);

    /**
     * 连接符号
     */
    private static final String CHANGE_FLAG = "->";

    /**
     * 忽略对比的字段
     */
    private static final Set<String> IGNORE_FIELDS = new HashSet<String>(){{
        add(Cons.FieldName.createTime.name());
    }};

    /***
     * 缓存BeanCopier
     */
    private static Map<String, BeanCopier> BEAN_COPIER_INST_MAP = new ConcurrentHashMap<>();

    /***
     * 获取实例
     * @param source
     * @param target
     * @return
     */
    private static BeanCopier getBeanCopierInstance(Object source, Object target){
        //build key
        String beanCopierKey =  source.getClass().toString() +"_"+ target.getClass().toString();
        BeanCopier copierInst =  BEAN_COPIER_INST_MAP.get(beanCopierKey);
        if(copierInst == null){
            copierInst = BeanCopier.create(source.getClass(), target.getClass(), false);
            BEAN_COPIER_INST_MAP.put(beanCopierKey, copierInst);
        }
        return copierInst;
    }

    /**
     * Copy属性到另一个对象
     * @param source
     * @param target
     */
    public static Object copyProperties(Object source, Object target){
        BeanCopier copierInst =  getBeanCopierInstance(source, target);
        copierInst.copy(source, target, null);
        return target;
    }

    /***
     * 将对象转换为另外的对象实例
     * @param source
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T convert(Object source, Class<T> clazz){
        if(source == null){
            return null;
        }
        T target = null;
        try{
            target = clazz.getConstructor().newInstance();
            copyProperties(source, target);
        }
        catch (Exception e){
            log.warn("对象转换异常, class="+clazz.getName());
        }
        return target;
    }

    /***
     * 将对象转换为另外的对象实例
     * @param sourceList
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> convertList(List sourceList, Class<T> clazz){
        if(V.isEmpty(sourceList)){
            return Collections.emptyList();
        }
        List<T> resultList = new ArrayList<>();
        // 类型相同，直接跳过
        if(clazz.getName().equals(sourceList.get(0).getClass().getName())){
            return sourceList;
        }
        // 不同，则转换
        try{
            for(Object source : sourceList){
                T target = clazz.getConstructor().newInstance();
                copyProperties(source, target);
                resultList.add(target);
            }
        }
        catch (Exception e){
            log.warn("对象转换异常, class="+clazz.getName());
        }
        return resultList;
    }

    /***
     * 附加Map中的属性值到Model
     * @param model
     * @param propMap
     */
    public static void bindProperties(Object model, Map<String, Object> propMap){
        try{// 获取类属性
            BeanInfo beanInfo = Introspector.getBeanInfo(model.getClass());
            // 给 JavaBean 对象的属性赋值
            PropertyDescriptor[] propertyDescriptors =  beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor descriptor : propertyDescriptors) {
                String propertyName = descriptor.getName();
                if (!propMap.containsKey(propertyName)){
                    continue;
                }
                Object value = propMap.get(propertyName);
                Class type = descriptor.getWriteMethod().getParameterTypes()[0];
                Object[] args = new Object[1];
                String fieldType = type.getName();
                // 类型不一致，需转型
                if(value != null && !value.getClass().getTypeName().equals(fieldType)){
                    if(value instanceof String){
                        // String to Date
                        if(fieldType.equals(Date.class.getName())){
                            args[0] = D.fuzzyConvert((String)value);
                        }
                        // Map中的String型转换为其他型
                        else if(fieldType.equals(Boolean.class.getName())){
                            args[0] = V.isTrue((String)value);
                        }
                        else if (fieldType.equals(Integer.class.getName()) || "int".equals(fieldType)) {
                            args[0] = Integer.parseInt((String)value);
                        }
                        else if (fieldType.equals(Long.class.getName())) {
                            args[0] = Long.parseLong((String)value);
                        }
                        else if (fieldType.equals(Double.class.getName())) {
                            args[0] = Double.parseDouble((String)value);
                        }
                        else if (fieldType.equals(Float.class.getName())) {
                            args[0] = Float.parseFloat((String)value);
                        }
                        else{
                            args[0] = value;
                            log.warn("类型不一致，暂无法自动绑定，请手动转型一致后调用！字段类型: {} vs {} ", value.getClass().getTypeName(), fieldType);
                        }
                    }
                    // Integer 向上转型为 Long 绑定
                    else if(value.getClass().getTypeName().equals(Integer.class.getName()) && fieldType.equals(Long.class.getName())){
                        Integer intValue = (Integer)value;
                        args[0] = intValue.longValue();
                    }
                    // Float 向上转型为 Double 绑定
                    else if(value.getClass().getTypeName().equals(Float.class.getName()) && fieldType.equals(Double.class.getName())){
                        Float floatValue = (Float)value;
                        args[0] = floatValue.doubleValue();
                    }
                    else{
                        args[0] = value;
                        log.warn("类型不一致，暂无法自动绑定，请手动转型一致后调用！ {} vs {} ", value.getClass().getTypeName(), fieldType);
                    }
                }
                else{
                    args[0] = value;
                }
                descriptor.getWriteMethod().invoke(model, args);
            }
        }
        catch (Exception e){
            log.warn("复制Map属性到Model异常: " + e.getMessage(), e);
        }
    }

    /***
     * 获取对象的属性值
     * @param obj
     * @param field
     * @return
     */
    public static Object getProperty(Object obj, String field){
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(obj);
        return wrapper.getPropertyValue(field);
    }

    /***
     * 获取对象的属性值并转换为String
     * @param obj
     * @param field
     * @return
     */
    public static String getStringProperty(Object obj, String field){
        Object property = getProperty(obj, field);
        if(property == null){
            return null;
        }
        return String.valueOf(property);
    }

    /***
     * 设置属性值
     * @param obj
     * @param field
     * @param value
     */
    public static void setProperty(Object obj, String field, Object value) {
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(obj);
        wrapper.setPropertyValue(field, value);
    }

    /***
     * Key-Object对象Map
     * @param allLists
     * @return
     */
    public static <T> Map<String, T> convertToStringKeyObjectMap(List<T> allLists, String... fields){
        if(allLists == null || allLists.isEmpty()){
            return null;
        }
        Map<String, T> allListMap = new LinkedHashMap<>(allLists.size());
        // 转换为map
        try{
            for(T model : allLists){
                String key = null;
                if(V.isEmpty(fields)){
                    //未指定字段，以id为key
                    key = getStringProperty(model, Cons.FieldName.id.name());
                }
                // 指定了一个字段，以该字段为key，类型同该字段
                else if(fields.length == 1){
                    key = getStringProperty(model, fields[0]);
                }
                else{ // 指定了多个字段，以字段S.join的结果为key，类型为String
                    List list = new ArrayList();
                    for(String fld : fields){
                        list.add(getProperty(model, fld));
                    }
                    key = S.join(list);
                }
                if(key != null){
                    allListMap.put(key, model);
                }
                else{
                    log.warn(model.getClass().getName() + " 的属性 "+fields[0]+" 值存在 null，转换结果需要确认!");
                }
            }
        }
        catch(Exception e){
            log.warn("转换key-model异常", e);
        }
        return allListMap;
    }

    /***
     * Key-Object-List列表Map
     * @param allLists
     * @param fields
     * @param <T>
     * @return
     */
    public static <T> Map<String, List<T>> convertToStringKeyObjectListMap(List<T> allLists, String... fields){
        if (allLists == null || allLists.isEmpty()){
            return null;
        }
        Map<String, List<T>> allListMap = new LinkedHashMap<>(allLists.size());
        // 转换为map
        try {
            for (T model : allLists){
                String key = null;
                if(V.isEmpty(fields)){
                    //未指定字段，以id为key
                    key = getStringProperty(model, Cons.FieldName.id.name());
                }
                // 指定了一个字段，以该字段为key，类型同该字段
                else if(fields.length == 1){
                    key = getStringProperty(model, fields[0]);
                }
                else{ // 指定了多个字段，以字段S.join的结果为key，类型为String
                    List list = new ArrayList();
                    for(String fld : fields){
                        list.add(getProperty(model, fld));
                    }
                    key = S.join(list);
                }
                if(key != null){
                    List<T> list = allListMap.get(key);
                    if (list == null){
                        list = new ArrayList<T>();
                        allListMap.put(key, list);
                    }
                    list.add(model);
                }
                else{
                    log.warn(model.getClass().getName() + " 的属性 "+fields[0]+" 值存在 null，转换结果需要确认!");
                }
            }
        } catch (Exception e){
            log.warn("转换key-model-list异常", e);
        }

        return allListMap;
    }

    /***
     * 构建上下级关联的树形结构的model（上级parentId、子节点children），根节点=0
     * @param allNodes 所有节点对象
     * @param <T>
     * @return
     */
    public static <T> List<T> buildTree(List<T> allNodes){
        return buildTree(allNodes, 0);
    }

    /***
     * 构建指定根节点的上下级关联的树形结构（上级parentId、子节点children）
     * @param allNodes 所有节点对象
     * @param rootNodeId 跟节点ID
     * @param <T>
     * @return
     */
    public static <T> List<T> buildTree(List<T> allNodes, Object rootNodeId){
        if(V.isEmpty(allNodes)){
            return null;
        }
        // 提取所有的top level对象
        List<T> topLevelModels = new ArrayList();
        for(T model : allNodes){
            Object parentId = getProperty(model, Cons.FieldName.parentId.name());
            if(parentId == null || V.fuzzyEqual(parentId, rootNodeId)){
                topLevelModels.add(model);
            }
        }
        if(V.isEmpty(topLevelModels)){
            return topLevelModels;
        }
        // 遍历第一级节点，并挂载 children 子节点
        for(T node : allNodes) {
            Object nodeId = getProperty(node, Cons.FieldName.id.name());
            List<T> children = buildTreeChildren(nodeId, allNodes);
            setProperty(node, Cons.FieldName.children.name(), children);
        }
        return topLevelModels;
    }

    /**
     * 递归构建树节点的子节点
     * @param parentId
     * @param nodeList
     * @return
     */
    private static <T> List<T> buildTreeChildren(Object parentId, List<T> nodeList) {
        List<T> children = null;
        for(T node : nodeList) {
            Object nodeParentId = getProperty(node, Cons.FieldName.parentId.name());
            if(nodeParentId != null && V.equals(nodeParentId, parentId)) {
                if(children == null){
                    children = new ArrayList<>();
                }
                children.add(node);
            }
        }
        if(children != null){
            for(T child : children) {
                Object nodeId = getProperty(child, Cons.FieldName.id.name());
                List<T> childNodeChildren = buildTreeChildren(nodeId, nodeList);
                if(childNodeChildren == null) {
                    childNodeChildren = new ArrayList<>();
                }
                setProperty(child, Cons.FieldName.children.name(), childNodeChildren);
            }
        }
        return children;
    }

    /***
     * 提取两个model的差异值
     * @param oldModel
     * @param newModel
     * @return
     */
    public static String extractDiff(BaseEntity oldModel, BaseEntity newModel){
        return extractDiff(oldModel, newModel, null);
    }

    /***
     * 提取两个model的差异值，只对比指定字段
     * @param oldModel
     * @param newModel
     * @return
     */
    public static String extractDiff(BaseEntity oldModel, BaseEntity newModel, Set<String> fields){
        if(newModel == null || oldModel == null){
            log.warn("调用错误，Model不能为空！");
            return null;
        }
        Map<String, Object> oldMap = oldModel.toMap();
        Map<String, Object> newMap = newModel.toMap();
        Map<String, Object> result = new HashMap<>(oldMap.size()+newMap.size());
        for(Map.Entry<String, Object> entry : oldMap.entrySet()){
            if(IGNORE_FIELDS.contains(entry.getKey())){
                continue;
            }
            String oldValue = entry.getValue()!=null ? String.valueOf(entry.getValue()) : "";
            Object newValueObj = newMap.get(entry.getKey());
            String newValue = newValueObj!=null? String.valueOf(newValueObj) : "";
            // 设置变更的值
            boolean checkThisField = fields == null || fields.contains(entry.getKey());
            if(checkThisField && !oldValue.equals(newValue)){
                result.put(entry.getKey(), S.join(oldValue, CHANGE_FLAG, newValue));
            }
            // 从新的map中移除该key
            if(newValueObj!=null){
                newMap.remove(entry.getKey());
            }
        }
        if(!newMap.isEmpty()){
            for(Map.Entry<String, Object> entry : newMap.entrySet()){
                if(IGNORE_FIELDS.contains(entry.getKey())){
                    continue;
                }
                Object newValueObj = entry.getValue();
                String newValue = newValueObj!=null? String.valueOf(newValueObj) : "";
                // 设置变更的值
                if(fields==null || fields.contains(entry.getKey())){
                    result.put(entry.getKey(), S.join("", CHANGE_FLAG, newValue));
                }
            }
        }
        oldMap = null;
        newMap = null;
        // 转换结果为String
        return JSON.toJSONString(result);
    }

    /**
     * 从list对象列表中提取指定属性值到新的List
     * @param objectList 对象list
     * @param getterFn get方法
     * @param <T>
     * @return
     */
    public static <E,T> List collectToList(List<E> objectList, IGetter<T> getterFn){
        if(V.isEmpty(objectList)){
           return Collections.emptyList();
        }
        String getterPropName = convertToFieldName(getterFn);
        return collectToList(objectList, getterPropName);
    }

    /**
     * 从list对象列表中提取Id主键值到新的List
     * @param objectList 对象list
     * @param <E>
     * @return
     */
    public static <E> List collectIdToList(List<E> objectList){
        if(V.isEmpty(objectList)){
            return Collections.emptyList();
        }
        return collectToList(objectList, Cons.FieldName.id.name());
    }

    /***
     * 从list对象列表中提取指定属性值到新的List
     * @param objectList
     * @param getterPropName
     * @param <E>
     * @return
     */
    public static <E> List collectToList(List<E> objectList, String getterPropName){
        List fieldValueList = new ArrayList();
        try{
            for(E object : objectList){
                Object fieldValue = getProperty(object, getterPropName);
                if(!fieldValueList.contains(fieldValue)){
                    fieldValueList.add(fieldValue);
                }
            }
        }
        catch (Exception e){
            log.warn("提取属性值异常, getterPropName="+getterPropName, e);
        }
        return fieldValueList;
    }

    /**
     * 绑定map中的属性值到list
     * @param setFieldFn
     * @param getFieldFun
     * @param fromList
     * @param valueMatchMap
     * @param <T1>
     */
    public static <T1,T2,R,E> void bindPropValueOfList(ISetter<T1,R> setFieldFn, List<E> fromList, IGetter<T2> getFieldFun, Map valueMatchMap){
        if(V.isEmpty(fromList)){
            return;
        }
        // function转换为字段名
        String setterFieldName = convertToFieldName(setFieldFn), getterFieldName = convertToFieldName(getFieldFun);
        bindPropValueOfList(setterFieldName, fromList, getterFieldName, valueMatchMap);
    }

    /***
     * 从对象集合提取某个属性值到list中
     * @param setterFieldName
     * @param fromList
     * @param getterFieldName
     * @param valueMatchMap
     * @param <E>
     */
    public static <E> void bindPropValueOfList(String setterFieldName, List<E> fromList, String getterFieldName, Map valueMatchMap){
        if(V.isEmpty(fromList) || V.isEmpty(valueMatchMap)){
            return;
        }
        try{
            for(E object : fromList){
                Object fieldValue = getProperty(object, getterFieldName);
                Object value = null;
                if(valueMatchMap.containsKey(fieldValue)){
                    value = valueMatchMap.get(fieldValue);
                }
                else{
                    // 获取到当前的属性值
                    String fieldValueStr = getStringProperty(object, getterFieldName);
                    // 获取到当前的value
                    value = valueMatchMap.get(fieldValueStr);
                }
                // 赋值
                setProperty(object, setterFieldName, value);
            }
        }
        catch (Exception e){
            log.warn("设置属性值异常, setterFieldName="+setterFieldName, e);
        }
    }

    /**
     * 克隆对象
     * @param ent
     * @param <T>
     * @return
     */
    public static <T> T cloneBean(T ent){
        // 克隆对象
        try{
            T cloneObj = (T)org.springframework.beans.BeanUtils.instantiateClass(ent.getClass());
            copyProperties(ent ,cloneObj);
            return cloneObj;
        }
        catch (Exception e){
            log.warn("Clone Object "+ent.getClass().getSimpleName()+" error", e);
            return ent;
        }
    }

    /***
     * 转换方法引用为属性名
     * @param fn
     * @return
     */
    public static <T> String convertToFieldName(IGetter<T> fn) {
        SerializedLambda lambda = getSerializedLambda(fn);
        String methodName = lambda.getImplMethodName();
        String prefix = null;
        if(methodName.startsWith("get")){
            prefix = "get";
        }
        else if(methodName.startsWith("is")){
            prefix = "is";
        }
        if(prefix == null){
            log.warn("无效的getter方法: "+methodName);
        }
        return S.uncapFirst(S.substringAfter(methodName, prefix));
    }

    /***
     * 转换方法引用为属性名
     * @param fn
     * @return
     */
    public static <T,R> String convertToFieldName(ISetter<T,R> fn) {
        SerializedLambda lambda = getSerializedLambda(fn);
        String methodName = lambda.getImplMethodName();
        if(!methodName.startsWith("set")){
            log.warn("无效的setter方法: "+methodName);
        }
        return S.uncapFirst(S.substringAfter(methodName, "set"));
    }

     /**
     * 获取类所有属性（包含父类中属性）
     * @param clazz
     * @return
     */
    public static List<Field> extractAllFields(Class clazz){
        List<Field> fieldList = new ArrayList<>();
        Set<String> fieldNameSet = new HashSet<>();
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            if(V.notEmpty(fields)){ //被重写属性，以子类override的为准
                Arrays.stream(fields).forEach((field)->{
                    if(!fieldNameSet.contains(field.getName())){
                        fieldList.add(field);
                        fieldNameSet.add(field.getName());
                    }
                });
            }
            clazz = clazz.getSuperclass();
        }
        return fieldList;
    }

    /**
     * 获取类的指定属性（包含父类中属性）
     * @param clazz
     * @param fieldName
     * @return
     */
    public static Field extractField(Class clazz, String fieldName){
        List<Field> allFields = extractAllFields(clazz);
        if(V.notEmpty(allFields)){
            for(Field field : allFields){
                if(field.getName().equals(fieldName)){
                    return field;
                }
            }
        }
        return null;
    }

    /**
     * 从宿主类定义中获取泛型定义类class
     * @param hostClass
     * @param index
     * @return
     */
    public static Class getGenericityClass(Class hostClass, int index){
        ResolvableType resolvableType = ResolvableType.forClass(hostClass).getSuperType();
        ResolvableType[] types = resolvableType.getSuperType().getGenerics();
        if(V.notEmpty(types) && types.length > index){
            return types[index].resolve();
        }
        log.warn("无法从 {} 类定义中获取泛型类{}", hostClass.getName(), index);
        return null;
    }

    /***
     * 获取类对应的Lambda
     * @param fn
     * @return
     */
    private static SerializedLambda getSerializedLambda(Serializable fn){
        SerializedLambda lambda = null;
        try{
            Method method = fn.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            lambda = (SerializedLambda) method.invoke(fn);
        }
        catch (Exception e){
            log.error("获取SerializedLambda异常, class="+fn.getClass().getSimpleName(), e);
        }
        return lambda;
    }

}
