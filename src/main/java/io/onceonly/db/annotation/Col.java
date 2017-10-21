package io.onceonly.db.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Col {
    boolean unique() default false;
    
    String using() default "BTREE";
    
    boolean nullable() default true;
    
    String pattern() default "";
    
    Class<?> valRef() default void.class;

    String colDef() default "";

    Class<?> ref() default void.class;
    /** 使用该子段是必须使用该entity的主键  */
    String refField() default "";
    /** 使用外键  */
    boolean useFK() default true;
    
    /**
     * (Optional) 
     * The string-valued column length. (Applies only if a
     * string-valued column is used.)
     * The decimal column scale for a decimal (exact numeric) column
     */
    int size() default 255;

    /**
     * (Optional) The precision for a decimal (exact numeric)
     * column. (Applies only if a decimal column is used.)
     * Value must be set by developer if used when generating
     * the DDL for the column.
     */
    int precision() default 0;

    /**
     * (Optional) The scale for a decimal (exact numeric) column.
     * (Applies only if a decimal column is used.)
     */
    int scale() default 0;
}
