package io.onceonly.db;

import java.util.List;

import io.onceonly.db.annotation.ConstraintType;

public class ConstraintMeta {
	public static final String PRIMARY_KEY = "PRIMARY KEY";
	public static final String FOREIGN_KEY = "FOREIGN KEY";
	public static final String UNIQUE = "UNIQUE";
	public static final String INDEX = "INDEX";

	String name;
	ConstraintType type;
	String using;
	String table;
	String refTable;
	List<String> columns;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ConstraintType getType() {
		return type;
	}
	public void setType(ConstraintType type) {
		this.type = type;
	}
	public String getUsing() {
		return using;
	}
	public void setUsing(String using) {
		this.using = using;
	}
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public String getRefTable() {
		return refTable;
	}
	public void setRefTable(String refTable) {
		this.refTable = refTable;
	}
	public List<String> getColumns() {
		return columns;
	}
	public void setColumns(List<String> columns) {
		this.columns = columns;
	}
	
}
