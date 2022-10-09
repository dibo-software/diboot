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
package com.diboot.core.util;

import com.diboot.core.exception.BusinessException;
import com.diboot.core.vo.Status;
import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Validator校验类
 *
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/01/01
 */
@SuppressWarnings("unused")
public class V {
    private static final Logger log = LoggerFactory.getLogger(V.class);
    /**
     * hibernate注解验证
     */
    private static Validator VALIDATOR = null;

    // region 判空方法
    /* ================================================== 判空方法 =================================================== */

    /**
     * 对象是否为空
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof CharSequence) {
            return isEmpty((CharSequence) obj);
        } else if (obj instanceof Collection) {
            return isEmpty((Collection<?>) obj);
        } else if (obj instanceof Map) {
            return isEmpty((Map<?, ?>) obj);
        } else if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        } else if (obj instanceof Iterator) {
            return isEmpty((Iterator<?>) obj);
        } else if (obj instanceof Iterable) {
            return isEmpty((Iterable<?>) obj);
        } else if (obj instanceof Enumeration) {
            return isEmpty((Enumeration<?>) obj);
        }
        return false;
    }

    /**
     * 字符串是否为空
     */
    public static boolean isEmpty(CharSequence value) {
        return S.isBlank(value);
    }

    /**
     * 字符串是否为空
     */
    public static boolean isEmpty(String value) {
        return S.isBlank(value);
    }

    /**
     * 集合为空
     */
    public static <T> boolean isEmpty(Collection<T> list) {
        return list == null || list.isEmpty();
    }

    /**
     * 字符串数组是否不为空
     */
    public static boolean isEmpty(String[] values) {
        return values == null || values.length == 0;
    }

    /**
     * Map为空
     */
    public static <T, F> boolean isEmpty(Map<T, F> obj) {
        return obj == null || obj.isEmpty();
    }

    public static <T> boolean isEmpty(Iterator<T> iterator) {
        return iterator == null || !iterator.hasNext();
    }

    public static <T> boolean isEmpty(Iterable<T> iterable) {
        return iterable == null || isEmpty(iterable.iterator());
    }

    public static <T> boolean isEmpty(Enumeration<T> enumeration) {
        return enumeration == null || !enumeration.hasMoreElements();
    }

    public static boolean isEmpty(boolean[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(byte[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(char[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(double[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(float[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(int[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(long[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(short[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 任意元素为空则返回true
     *
     * @param objs objs
     * @return true/false
     */
    public static boolean isAnyEmpty(Object... objs) {
        if (isEmpty(objs)) {
            return true;
        }
        for (Object obj : objs) {
            if (isEmpty(obj)) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean isAnyEmpty(Collection<T> collection) {
        if (isEmpty(collection)) {
            return true;
        }
        for (T obj : collection) {
            if (isEmpty(obj)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 全为空则返回true
     *
     * @param objs objs
     * @return true/false
     */
    public static boolean isAllEmpty(Object... objs) {
        if (isEmpty(objs)) {
            return true;
        }
        for (Object obj : objs) {
            if (notEmpty(obj)) {
                return false;
            }
        }
        return true;
    }

    public static <T> boolean isAllEmpty(Collection<T> collection) {
        if (isEmpty(collection)) {
            return true;
        }
        for (T obj : collection) {
            if (notEmpty(obj)) {
                return false;
            }
        }
        return true;
    }
    /* ================================================== end ======================================================= */
    // endregion

    // region 非空判断方法
    /* ================================================== 非空判断方法 ================================================ */

    /**
     * 对象是否为空
     */
    public static boolean notEmpty(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj instanceof CharSequence) {
            return notEmpty((CharSequence) obj);
        } else if (obj instanceof Collection) {
            return notEmpty((Collection<?>) obj);
        } else if (obj instanceof Map) {
            return notEmpty((Map<?, ?>) obj);
        } else if (obj.getClass().isArray()) {
            return Array.getLength(obj) != 0;
        } else if (obj instanceof Iterator) {
            return notEmpty((Iterator<?>) obj);
        } else if (obj instanceof Iterable) {
            return notEmpty((Iterable<?>) obj);
        } else if (obj instanceof Enumeration) {
            return notEmpty((Enumeration<?>) obj);
        }
        return true;
    }

    /**
     * 字符串不为空，或者不是所有字符都为whitespace字符
     */
    public static boolean notEmpty(CharSequence value) {
        return S.isNotBlank(value);
    }

    /**
     * 字符串不为空，或者不是所有字符都为whitespace字符
     */
    public static boolean notEmpty(String value) {
        return S.isNotBlank(value);
    }

    /**
     * 字符串数组是否不为空
     */
    public static boolean notEmpty(String[] values) {
        return values != null && values.length > 0;
    }

    /**
     * 集合不为空
     */
    public static <T> boolean notEmpty(Collection<T> list) {
        return list != null && !list.isEmpty();
    }

    /**
     * Map不为空
     */
    public static <T, F> boolean notEmpty(Map<T, F> obj) {
        return obj != null && !obj.isEmpty();
    }

    public static <T> boolean notEmpty(Iterator<T> iterator) {
        return iterator != null && iterator.hasNext();
    }

    public static <T> boolean notEmpty(Iterable<T> iterable) {
        return iterable != null && notEmpty(iterable.iterator());
    }

    public static <T> boolean notEmpty(Enumeration<T> enumeration) {
        return enumeration != null && enumeration.hasMoreElements();
    }

    public static boolean notEmpty(boolean[] array) {
        return array != null && array.length != 0;
    }

    public static boolean notEmpty(byte[] array) {
        return array != null && array.length != 0;
    }

    public static boolean notEmpty(char[] array) {
        return array != null && array.length != 0;
    }

    public static boolean notEmpty(short[] array) {
        return array != null && array.length != 0;
    }

    public static boolean notEmpty(int[] array) {
        return array != null && array.length != 0;
    }

    public static boolean notEmpty(long[] array) {
        return array != null && array.length != 0;
    }

    public static boolean notEmpty(double[] array) {
        return array != null && array.length != 0;
    }

    public static boolean notEmpty(float[] array) {
        return array != null && array.length != 0;
    }

    public static boolean notEmpty(Object[] array) {
        return array != null && array.length != 0;
    }

    /**
     * 对象不为空且不为0
     */
    public static boolean notEmptyOrZero(Long longObj) {
        return longObj != null && longObj != 0;
    }

    /**
     * 对象不为空且不为0
     */
    public static boolean notEmptyOrZero(Integer intObj) {
        return intObj != null && intObj != 0;
    }

    /**
     * 全都不为空则返回true
     *
     * @param objs objs
     * @return true/false
     */
    public static boolean isNoneEmpty(Object... objs) {
        return !isAnyEmpty(objs);
    }

    public static <T> boolean isNoneEmpty(Collection<T> collection) {
        return !isAnyEmpty(collection);
    }
    /* ================================================== end ======================================================= */
    // endregion

    /**
     * 集合中是否包含指定元素
     *
     * @param collection 集合
     * @param target     查找元素
     * @return 集合为空或者不包含元素，则返回false
     */
    public static <T> boolean contains(Collection<T> collection, T target) {
        return collection != null && collection.contains(target);
    }

    /**
     * 集合中是否包含指定字符串或其大写字符串
     *
     * @param collection 集合
     * @param target     查找字符串
     * @return 集合为空或者不包含元素，则返回false
     */
    public static boolean containsIgnoreCase(Collection<String> collection, String target) {
        return collection != null && (collection.contains(target) || collection.contains(target.toLowerCase()) || collection.contains(target.toUpperCase()));
    }

    /**
     * 集合中是否不包含指定元素
     *
     * @param collection 集合
     * @param target     查找元素
     * @return 集合为空或者包含元素，则返回false
     */
    public static <T> boolean notContains(Collection<T> collection, T target) {
        return collection != null && !collection.contains(target);
    }

    /**
     * 集合中是否不包含指定字符串或其大写字符串
     *
     * @param collection 集合
     * @param target     查找字符串
     * @return 集合为空或者不包含元素，则返回false
     */
    public static boolean notContainsIgnoreCase(Collection<String> collection, String target) {
        return !(containsIgnoreCase(collection, target));
    }

    /**
     * 判断是否为数字（允许小数点）
     *
     * @return true Or false
     */
    public static boolean isNumber(String str) {
        String regex = "^(-?[1-9]\\d*\\.?\\d*)|(-?0\\.\\d*[1-9])|(-?[0])|(-?[0]\\.\\d*)$";
        return str.matches(regex);
    }

    /**
     * 判断是否为正确的邮件格式
     *
     * @return boolean
     */
    public static boolean isEmail(String str) {
        if (isEmpty(str)) {
            return false;
        }
        return str.matches("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$");
    }

    /**
     * 判断字符串是否为电话号码
     *
     * @return boolean
     */
    public static boolean isPhone(String str) {
        if (isEmpty(str)) {
            return false;
        }
        boolean valid = str.matches("^1\\d{10}$");
        if (!valid) {
            valid = str.matches("^[0|4]\\d{2,3}-?\\d{7,8}$");
        }
        return valid;
    }

    /**
     * 对参数值做安全检查
     */
    public static void securityCheck(String... paramValues) {
        if (isEmpty(paramValues)) {
            return;
        }
        for (String param : paramValues) {
            if (!V.isValidSqlParam(param)) {
                throw new BusinessException(Status.FAIL_VALIDATION, "非法的参数: " + param);
            }
        }
    }

    /**
     * 是否为合法的数据库列参数（orderBy等参数安全检查）
     */
    private static final Pattern PATTERN = Pattern.compile("^[A-Za-z_][\\w.:]*$");

    public static boolean isValidSqlParam(String columnStr) {
        if (isEmpty(columnStr)) {
            return true;
        }
        return PATTERN.matcher(columnStr).matches();
    }

    /**
     * 是否boolean值范围
     */
    private static final Set<String> TRUE_SET = new HashSet<>(Arrays.asList(
            "true", "是", "y", "yes", "1"
    ));

    private static final Set<String> FALSE_SET = new HashSet<>(Arrays.asList(
            "false", "否", "n", "no", "0"
    ));

    /**
     * 是否为boolean类型
     */
    public static boolean isValidBoolean(String value) {
        if (value == null) {
            return false;
        }
        value = S.trim(value).toLowerCase();
        return TRUE_SET.contains(value) || FALSE_SET.contains(value);
    }

    /**
     * 转换为boolean类型, 并判定是否为true
     */
    public static boolean isTrue(String value) {
        if (value == null) {
            return false;
        }
        value = S.trim(value).toLowerCase();
        return TRUE_SET.contains(value);
    }

    /**
     * 是否是false
     * <p>不要再写"xxx == false"了</p>
     *
     * @param expression bool表达式
     * @return 入参为false时返回true
     */
    public static boolean isFalse(boolean expression) {
        return !expression;
    }

    /**
     * 根据指定规则校验字符串的值是否合法
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Deprecated
    public static String validate(String value, String validation) {
        if (isEmpty(validation)) {
            return null;
        }
        List<String> errorMsgList = new ArrayList<>();
        String[] rules = validation.split(",");
        for (String rule : rules) {
            if ("NotNull".equalsIgnoreCase(rule)) {
                if (isEmpty(value)) {
                    errorMsgList.add("不能为空");
                }
            } else if ("Number".equalsIgnoreCase(rule)) {
                if (!isNumber(value)) {
                    errorMsgList.add("非数字格式");
                }
            } else if ("Boolean".equalsIgnoreCase(rule)) {
                if (!isValidBoolean(value)) {
                    errorMsgList.add("非Boolean格式");
                }
            } else if ("Date".equalsIgnoreCase(rule)) {
                if (D.fuzzyConvert(value) == null) {
                    errorMsgList.add("非日期格式");
                }
            } else if (rule.toLowerCase().startsWith("length")) {
                String range = rule.substring(rule.indexOf("(") + 1, rule.lastIndexOf(")"));
                if (range.contains("-")) {
                    String[] arr = range.split("-");
                    if (notEmpty(arr[0])) {
                        if (V.isEmpty(value) || value.length() < Integer.parseInt(arr[0])) {
                            errorMsgList.add("长度少于最小限制数: " + arr[0]);
                        }
                    }
                    if (notEmpty(arr[1])) {
                        if (V.notEmpty(value)) {
                            if (value.length() > Integer.parseInt(arr[1])) {
                                errorMsgList.add("长度超出最大限制数: " + arr[1]);
                            }
                        }
                    }
                } else {
                    if (V.isEmpty(value) || value.length() != Integer.parseInt(range)) {
                        errorMsgList.add("长度限制: " + range + "位");
                    }
                }
            } else if ("Email".equalsIgnoreCase(rule)) {
                if (!isEmail(value)) {
                    errorMsgList.add("非Email格式");
                }
            } else if ("Phone".equalsIgnoreCase(rule)) {
                if (!isPhone(value)) {
                    errorMsgList.add("非电话号码格式");
                }
            } else {
                // 无法识别的格式
            }
        }
        // 返回校验不通过的结果
        if (errorMsgList.isEmpty()) {
            return null;
        } else {
            return S.join(errorMsgList);
        }
    }

    /**
     * 判定两个对象是否不同类型或不同值
     */
    public static boolean notEquals(Object source, Object target) {
        return !equals(source, target);
    }

    /**
     * 判定两个对象是否类型相同值相等
     */
    public static <T> boolean equals(T source, T target) {
        if (source == target) {
            return true;
        } else if (source == null || target == null) {
            return false;
        }
        else if (source.getClass() != target.getClass() && !(source instanceof Map && target instanceof Map)) {
            // 根据equals设计原则，类型不一致，直接false（仅允许HashMap、LinkedHashMap的差异）
            // 避免子类与父类、子类与子类比较时可能出现的问题
            log.warn("source和target类型不匹配：" + source.getClass() + " 和 " + target.getClass());
            return false;
        } else if (source instanceof Class) {
            return ((Class<?>) source).getName().equals(((Class<?>) target).getName());
        } else if (source instanceof Comparable) {
            // 部分java早期类的equals和compareTo结果不一致，避免直接调用equals
            //noinspection unchecked
            return ((Comparable<T>) source).compareTo(target) == 0;
        } else if (source instanceof Collection) {
            Collection<?> sourceList = (Collection<?>) source, targetList = (Collection<?>) target;
            // size不等
            if (sourceList.size() != targetList.size()) {
                return false;
            } else if (sourceList.isEmpty()) {
                // size相等，且一个为空集合
                return true;
            }
            if (source instanceof Set) {
                //noinspection SuspiciousMethodCalls
                return ((Set<?>) source).containsAll(targetList);
            }

            // copy from org.apache.commons.collections4.CollectionUtils.isEqualCollection()
            // 分别统计 Collection中 元素对应的数量
            CardinalityHelper<Object> helper = new CardinalityHelper<>(sourceList, targetList);
            // 如果 两个Collection的统计结果 不一致
            if (helper.cardinalityA.size() != helper.cardinalityB.size()) {
                return false;
            }
            // 遍历统计结果
            for (final Object obj : helper.cardinalityA.keySet()) {
                // 相同元素 在两个Collection中的数量却不等
                if (helper.freqA(obj) != helper.freqB(obj)) {
                    return false;
                }
            }
            return true;
        } else if (source instanceof Map) {
            Map<?, ?> sourceMap = (Map<?, ?>) source, targetMap = (Map<?, ?>) target;
            if (sourceMap.size() != targetMap.size()) {
                return false;
            } else if (sourceMap.isEmpty()) {
                return true;
            }
            for (Map.Entry<?, ?> entry : sourceMap.entrySet()) {
                if (!equals(entry.getValue(), targetMap.get(entry.getKey()))) {
                    return false;
                }
            }
            return true;
        } else {
            log.warn("暂未实现类型 " + source.getClass().getName() + "-" + target.getClass().getName() + " 的比对！");
            return false;
        }
    }

    /**
     * 列表others中是否包含source
     *
     * @param source 查询对象
     * @param others 可能值列表
     * @param <T>    类型
     * @return others列表为空或者不包含source，则返回false
     */
    @SafeVarargs
    public static <T> boolean anyEquals(T source, T... others) {
        if (isEmpty(others)) {
            return false;
        }
        for (T other : others) {
            if (equals(source, other)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 模糊对比是否相等（类型不同的转成String对比）
     */
    public static boolean fuzzyEqual(Object source, Object target) {
        if (equals(source, target)) {
            return true;
        }
        // Boolean-String类型
        if (source instanceof Boolean && target instanceof String) {
            return (boolean) source == V.isTrue((String) target);
        }
        if (target instanceof Boolean && source instanceof String) {
            return (boolean) target == V.isTrue((String) source);
        }
        // Date-String类型
        else if (source instanceof Date && target instanceof String) {
            return D.getDateTime((Date) source).equals(target) || D.getDate((Date) source).equals(target);
        } else if (target instanceof Date && source instanceof String) {
            return D.getDateTime((Date) target).equals(source) || D.getDate((Date) target).equals(source);
        } else {
            return S.valueOf(source).equals(S.valueOf(target));
        }
    }

    /**
     * 解析所有的验证错误信息，转换为JSON
     */
    public static String getBindingError(BindingResult result) {
        if (result == null || !result.hasErrors()) {
            return null;
        }
        List<ObjectError> errors = result.getAllErrors();
        List<String> allErrors = new ArrayList<>(errors.size());
        String defaultMessage;
        for (ObjectError error : errors) {
            if (notEmpty(defaultMessage = error.getDefaultMessage())) {
                allErrors.add(defaultMessage.replaceAll("\"", "'"));
            }
        }
        return S.join(allErrors);
    }

    /**
     * 基于Bean中的validator注解校验
     *
     * @return 违法约束的集合
     */
    public static <T> Set<ConstraintViolation<T>> validateBean(T obj, Class<?>... groups) {
        if (VALIDATOR == null) {
            VALIDATOR = Validation.byProvider(HibernateValidator.class).configure().failFast(false).buildValidatorFactory().getValidator();
        }
        return VALIDATOR.validate(obj, groups);
    }

    /**
     * 基于Bean中的validator注解校验
     *
     * @return 违法约束的消息内容
     */
    public static <T> String validateBeanErrMsg(T obj, Class<?>... groups) {
        Set<ConstraintViolation<T>> errors = validateBean(obj, groups);
        if (isEmpty(errors)) {
            return null;
        }
        List<String> allErrors = new ArrayList<>(errors.size());
        for (ConstraintViolation<T> err : errors) {
            allErrors.add(err.getMessage());
        }
        return S.join(allErrors);
    }

    /**
     * Helper class to easily access cardinality properties of two collections.
     *
     * @param <O> the element type
     */
    private static class CardinalityHelper<O> {

        /**
         * Contains the cardinality for each object in collection A.
         */
        final Map<O, Integer> cardinalityA;

        /**
         * Contains the cardinality for each object in collection B.
         */
        final Map<O, Integer> cardinalityB;

        /**
         * Create a new CardinalityHelper for two collections.
         *
         * @param a the first collection
         * @param b the second collection
         */
        CardinalityHelper(final Iterable<? extends O> a, final Iterable<? extends O> b) {
            cardinalityA = getCardinalityMap(a);
            cardinalityB = getCardinalityMap(b);
        }

        /**
         * Returns a {@link Map} mapping each unique element in the given
         * {@link Collection} to an {@link Integer} representing the number
         * of occurrences of that element in the {@link Collection}.
         * <p>
         * Only those elements present in the collection will appear as
         * keys in the map.
         * </p>
         *
         * @param <O>  the type of object in the returned {@link Map}. This is a super type of &lt;I&gt;.
         * @param coll the collection to get the cardinality map for, must not be null
         * @return the populated cardinality map
         * @throws NullPointerException if coll is null
         */
        public static <O> Map<O, Integer> getCardinalityMap(final Iterable<? extends O> coll) {
            Objects.requireNonNull(coll, "coll");
            final Map<O, Integer> count = new HashMap<>();
            for (final O obj : coll) {
                count.merge(obj, 1, Integer::sum);
            }
            return count;
        }

        /**
         * Returns the maximum frequency of an object.
         *
         * @param obj the object
         * @return the maximum frequency of the object
         */
        public final int max(final Object obj) {
            return Math.max(freqA(obj), freqB(obj));
        }

        /**
         * Returns the minimum frequency of an object.
         *
         * @param obj the object
         * @return the minimum frequency of the object
         */
        public final int min(final Object obj) {
            return Math.min(freqA(obj), freqB(obj));
        }

        /**
         * Returns the frequency of this object in collection A.
         *
         * @param obj the object
         * @return the frequency of the object in collection A
         */
        public int freqA(final Object obj) {
            return getFreq(obj, cardinalityA);
        }

        /**
         * Returns the frequency of this object in collection B.
         *
         * @param obj the object
         * @return the frequency of the object in collection B
         */
        public int freqB(final Object obj) {
            return getFreq(obj, cardinalityB);
        }

        private int getFreq(final Object obj, final Map<?, Integer> freqMap) {
            final Integer count = freqMap.get(obj);
            if (count != null) {
                return count;
            }
            return 0;
        }
    }

}
