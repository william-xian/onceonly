package io.onceonly.db.dao;

import io.onceonly.util.Tuple2;

/**
 * 返回需要匹配的字段
 */
public interface TemplateAdapter {
	<E> Tuple2<String[],Object[]> adapterForUpdate(E tmpl);
	
	/**
	 * 返回实际的数据，这里会处理del字段,
	 */
	<E> Tuple2<String[],Object[]> adapterForWhere(E tmpl);
	
	<E> Tuple2<String[],Object[]> adapterForSelect(E tmpl); 
}