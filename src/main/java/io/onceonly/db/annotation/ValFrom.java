package io.onceonly.db.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** 枚举等常量的引用关系   */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
@Documented
public @interface ValFrom {
    Class<?> clazz();
}
