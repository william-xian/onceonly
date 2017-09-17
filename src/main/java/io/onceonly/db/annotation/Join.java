package io.onceonly.db.annotation;

public @interface Join {
	Class<?> left() default void.class;
	Class<?> right() default void.class;
	String alias();
	Class<?> target() default void.class;
	String tAlias() default "";
	/** 默认主键相同的有效数据 a.id=b.id and a.del = false */
	String cnd() default "";
}
