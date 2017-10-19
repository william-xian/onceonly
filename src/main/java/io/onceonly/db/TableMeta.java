package io.onceonly.db;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class TableMeta {
	private Class<?> entity;
	private List<String> index;
	private Map<String,ColumnMeta> columnMeta;
	
	public String createTableSql() {
		//TODO
		return null;
	}
}

class ColumnMeta {
	String name;
	String type;
	String alias;
	boolean nullable;
	/**
	 * 正则表达式 或@JSON,@Email等特定教研
	 */
	String pattern;
	Field field;
}
