package io.onceonly.db.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({METHOD, FIELD})
@Retention(RUNTIME)
@Documented
public @interface Ref {
    Class<?> entity();
    /** 使用该子段是必须使用该entity的主键  */
    String fieldName() default "";
    /** 使用外键  */
    boolean useFK() default true;
    
    boolean nullable() default false;
}
