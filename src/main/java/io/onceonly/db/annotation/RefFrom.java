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
public @interface RefFrom {
    Class<?> entity();
    String alias() default "";
    /** 使用外键  */
    boolean useFK() default true;
}
