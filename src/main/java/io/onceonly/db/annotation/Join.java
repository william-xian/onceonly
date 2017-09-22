package io.onceonly.db.annotation;

public @interface Join {
	Class<?> left() default void.class;
	Class<?> right() default void.class;
	String alias();
	Class<?> target() default void.class;
	String tAlias() default "";
	/**
	 * 两个表的主键或者外键相等
	 * 默认主键相同的有效数据 a.id=b.id
	 */
	String cnd() default "";
}
