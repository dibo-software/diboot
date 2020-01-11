package com.diboot.core.binding;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.diboot.core.config.Cons;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;

/**
 * QueryWrapper构建器 - Entity，DTO -> 注解绑定查询条件 并转换为QueryWrapper对象
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/07/27
 */
public class QueryBuilder {
    private static Logger log = LoggerFactory.getLogger(QueryBuilder.class);

    /**
     * Entity或者DTO对象转换为QueryWrapper
     * @param dto
     * @param <T>
     * @param <DTO>
     * @return
     */
    public static <T,DTO> QueryWrapper<T> toQueryWrapper(DTO dto){
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        return (QueryWrapper<T>) dtoToWrapper(wrapper, dto, null);
    }

    /**
     * Entity或者DTO对象转换为QueryWrapper
     * @param dto
     * @param fields 指定参与转换的属性值
     * @param <T>
     * @param <DTO>
     * @return
     */
    public static <T,DTO> QueryWrapper<T> toQueryWrapper(DTO dto, Collection<String> fields){
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        return (QueryWrapper<T>) dtoToWrapper(wrapper, dto, fields);
    }

    /**
     * Entity或者DTO对象转换为LambdaQueryWrapper
     * @param dto
     * @param <T>
     * @return
     */
    public static <T,DTO> LambdaQueryWrapper<T> toLambdaQueryWrapper(DTO dto){
        return (LambdaQueryWrapper<T>) toQueryWrapper(dto).lambda();
    }


    /**
     * Entity或者DTO对象转换为LambdaQueryWrapper
     * @param dto
     * @param fields 指定参与转换的属性值
     * @param <T>
     * @return
     */
    public static <T,DTO> LambdaQueryWrapper<T> toLambdaQueryWrapper(DTO dto, Collection<String> fields){
        return (LambdaQueryWrapper<T>) toQueryWrapper(dto, fields).lambda();
    }

    /**
     * 转换具体实现
     * @param wrapper
     * @param dto
     * @param <T>
     * @return
     */
    private static <T,DTO> QueryWrapper<T> dtoToWrapper(QueryWrapper wrapper, DTO dto, Collection<String> fields){
        // 转换
        List<Field> declaredFields = BeanUtils.extractAllFields(dto.getClass());
        for (Field field : declaredFields) {
            // 非指定属性，非逻辑删除字段，跳过
            if(fields != null && !fields.contains(field.getName())){
                continue;
            }
            //忽略static，以及final，transient
            boolean isStatic = Modifier.isStatic(field.getModifiers());
            boolean isFinal = Modifier.isFinal(field.getModifiers());
            boolean isTransient = Modifier.isTransient(field.getModifiers());
            if(isStatic || isFinal || isTransient){
                continue;
            }
            //忽略注解 @TableField(exist = false) 的字段
            TableField tableField = field.getAnnotation(TableField.class);
            if(tableField != null && tableField.exist() == false){
                continue;
            }
            BindQuery query = field.getAnnotation(BindQuery.class);
            if(query != null && query.ignore()){ //忽略字段
                continue;
            }
            //打开私有访问 获取值
            field.setAccessible(true);
            Object value = null;
            try {
                value = field.get(dto);
            }
            catch (IllegalAccessException e) {
                log.error("通过反射获取属性值出错：" + e);
            }
            if(value == null){
                continue;
            }
            // 对比类型
            Comparison comparison = (query != null)? query.comparison() : Comparison.EQ;
            // 转换条件
            String columnName = getColumnName(field);
            switch (comparison) {
                case EQ:
                    wrapper.eq(columnName, value);
                    break;
                case IN:
                    if(value.getClass().isArray()){
                        Object[] valueArray = (Object[])value;
                        if(valueArray.length == 1){
                            wrapper.in(columnName, valueArray[0]);
                        }
                        else if(valueArray.length >= 2){
                            wrapper.in(columnName, valueArray);
                        }
                    }
                    else{
                        wrapper.in(columnName, value);
                    }
                    break;
                case CONTAINS:
                    wrapper.like(columnName, value);
                    break;
                case LIKE:
                    wrapper.like(columnName, value);
                    break;
                case STARTSWITH:
                    wrapper.likeRight(columnName, value);
                    break;
                case GT:
                    wrapper.gt(columnName, value);
                    break;
                case BETWEEN_BEGIN:
                    wrapper.ge(columnName, value);
                    break;
                case GE:
                    wrapper.ge(columnName, value);
                    break;
                case LT:
                    wrapper.lt(columnName, value);
                    break;
                case BETWEEN_END:
                    wrapper.le(columnName, value);
                    break;
                case LE:
                    wrapper.le(columnName, value);
                    break;
                case BETWEEN:
                    if(value.getClass().isArray()){
                        Object[] valueArray = (Object[])value;
                        if(valueArray.length == 1){
                            wrapper.ge(columnName, valueArray[0]);
                        }
                        else if(valueArray.length >= 2){
                            wrapper.between(columnName, valueArray[0], valueArray[1]);
                        }
                    }
                    // 支持逗号分隔的字符串
                    else if(value instanceof String && ((String) value).contains(",")){
                        Object[] valueArray = ((String) value).split(",");
                        wrapper.between(columnName, valueArray[0], valueArray[1]);
                    }
                    else{
                        wrapper.ge(columnName, value);
                    }
                    break;
                default:
            }
        }
        return wrapper;
    }

    /**
     * 获取数据表的列名（驼峰转下划线蛇形命名）
     * <br>
     * 列名取值优先级： @BindQuery.field > @TableField.value > field.name
     *
     * @param field
     * @return
     */
    private static String getColumnName(Field field){
        String columnName = null;
        if (field.isAnnotationPresent(BindQuery.class)) {
            columnName = field.getAnnotation(BindQuery.class).field();
        }
        else if (field.isAnnotationPresent(TableField.class)) {
            columnName = field.getAnnotation(TableField.class).value();
        }
        return V.notEmpty(columnName) ? columnName : S.toSnakeCase(field.getName());
    }

}