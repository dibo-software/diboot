package com.diboot.file.excel.annotation;

import org.apache.poi.ss.usermodel.IndexedColors;

import java.lang.annotation.*;

/**
 * Excel 颜色
 */
@Documented
@Target(ElementType.FIELD)
@Repeatable(ExcelColor.List.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelColor {

    /**
     * 正则匹配
     */
    String regex();

    /**
     * 字体颜色
     */
    IndexedColors fontColor() default IndexedColors.AUTOMATIC;

    /**
     * 背景颜色
     */
    IndexedColors backgroundColor() default IndexedColors.AUTOMATIC;

    /**
     * 在同一个字段上支持多个{@link ExcelColor}，由上到下依次填充
     *
     * @author wind
     * @since v2.4.0
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {

        ExcelColor[] value();

    }
}
