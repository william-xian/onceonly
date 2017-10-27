package io.onceonly.db.dao;

import io.onceonly.util.Tuple2;

/**
 * 返回需要匹配的字段
 */
public interface TemplateAdapter {
	<E> Tuple2<String[],Object[]> adapterForUpdate(E tmpl);
	<E> Tuple2<String[],Object[]> adapterForWhere(E tmpl);
	<E> Tuple2<String[],Object[]> adapterForSelect(E tmpl); 
}