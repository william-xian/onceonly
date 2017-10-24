package io.onceonly.db.meta;

public class ColumnMeta {
	String name;
	String type;
	boolean nullable;
	boolean unique;
	String refTable;
	String refField;
	boolean useFK;
	String using;
	boolean primaryKey;
	
	/**
	 * 正则表达式 或@JSON,@Email等特定教研
	 */
	String pattern;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isNullable() {
		return nullable;
	}
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}
	public boolean isUnique() {
		return unique;
	}
	public void setUnique(boolean unique) {
		this.unique = unique;
	}
	public String getRefTable() {
		return refTable;
	}
	public void setRefTable(String refTable) {
		this.refTable = refTable;
	}
	public String getRefField() {
		return refField;
	}
	public void setRefField(String refField) {
		this.refField = refField;
	}
	public boolean isUseFK() {
		return useFK;
	}
	public void setUseFK(boolean useFK) {
		this.useFK = useFK;
	}
	public String getUsing() {
		return using;
	}
	public void setUsing(String using) {
		this.using = using;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public boolean isPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}
}