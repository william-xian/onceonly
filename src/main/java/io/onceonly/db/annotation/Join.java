package io.onceonly.db.annotation;

public @interface Join {
	Class<?> from();
	String fAlias() default "";
	Class<?> to();
	String tAlias() default "";
	String cnd() default "";
}
