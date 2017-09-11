package io.onceonly.db.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

@Target({FIELD})
@Retention(RUNTIME)
public @interface VColumn {
	@AliasFor("name")
	String value();
	@AliasFor("value")
	String name() default "";
}
