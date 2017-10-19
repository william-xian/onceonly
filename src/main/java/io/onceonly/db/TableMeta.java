package io.onceonly.db;

import java.util.List;
import java.util.Map;

public class TableMeta {
	String table;
	String primaryKey;
	List<String[]> uniqueConstraint;
	Map<String,ColumnMeta> columnMeta;
	/** drop table if exists tbl_a;*/
	public String createTableSql() {
		StringBuffer tbl = new StringBuffer();
		tbl.append(String.format("CREATE TABLE %s (", table));
		StringBuffer FKs =  new StringBuffer();
		for(ColumnMeta cm:columnMeta.values()) {
			tbl.append(String.format("%s %s%s%s,", cm.name,cm.type, cm.nullable?"":" not null",cm.unique?" unique":""));
			if(cm.useFK && cm.refTable !=null){
				FKs.append(String.format("FOREIGN KEY (%s) REFERENCES %s(%s),",cm.name,cm.refTable,cm.name));
			}
		}
		for(String[] tuple:uniqueConstraint) {
			tbl.append(String.format("CONSTRAINT %s_%s UNIQUE (%s)",table,String.join("_", tuple),String.join(",", tuple)));
		}
		if(primaryKey != null) {
			tbl.append("PRIMARY Key id,");	
		}
		tbl.append(FKs.toString());
		tbl.delete(tbl.length()-1, tbl.length());
		tbl.append(")");
		return tbl.toString();
	}

	/**
	 * TODO
	 * 升级数据库，返回需要执行的sql
	 * @param other
	 * @return
	 */
	public String upgradeTo(TableMeta other) {
		StringBuffer sql = new StringBuffer();
		return sql.toString();
	}
	/**
	 * TODO
	 * 升级数据库，废弃的数据库字段不需要使用了，删除操作sql
	 * @param other
	 * @return
	 */
	public String cleanByTo(TableMeta other) {
		StringBuffer sql = new StringBuffer();
		return sql.toString();
	}
}

class ColumnMeta {
	String name;
	String type;
	boolean nullable;
	boolean unique;
	String refTable;
	boolean useFK;
	/**
	 * 正则表达式 或@JSON,@Email等特定教研
	 */
	String pattern;
}
