package io.onceonly.db;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TableMeta {
	String table;
	/** 复合主键以逗号隔开不可有空格*/
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
			tbl.append(String.format("PRIMARY Key (%s),",primaryKey));	
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
		if(!table.equals(other.table)) {
			return null;
		}
		StringBuffer sql = new StringBuffer();
		if(primaryKey != null && primaryKey.equals(other.primaryKey)) {
			sql.append(String.format("alter table %s drop constraint %;",table,primaryKey.replace(',', '_')));
		}
		if(other.primaryKey != null && other.primaryKey.equals(primaryKey)) {
			sql.append(String.format("alter table %s add constraint %s primary key (%s);",table,other.primaryKey.replace(',', '_'),other.primaryKey));
		}
		Map<String,ColumnMeta> otherColumn = other.columnMeta;
		for(ColumnMeta ocm:otherColumn.values()) {
			ColumnMeta cm = columnMeta.get(ocm.name);
			if(cm == null) {
				sql.append(String.format("alter table %s add %s %s%s%s;",table, ocm.name,ocm.type, ocm.nullable?"":" not null",ocm.unique?" unique":""));
				if(ocm.useFK && ocm.refTable !=null){
					sql.append(String.format("ALTER TABLE %s ADD CONSTRAINT fk_%s FOREIGN KEY (%s) REFERENCES %s(%s);",table,ocm.name,ocm.name,ocm.refTable,ocm.name));
				}
			}else {
				//需要删除约束 TODO
				if(cm.unique &&!ocm.unique) {
					sql.append(String.format("alter table %s drop constraint %s;",table, ocm.name));		
				}
				//TODO
				if(cm.useFK && !ocm.useFK) {
				}
				if(!cm.type.equals(ocm.type) || cm.nullable != ocm.nullable) {
					sql.append(String.format("alter table %s alter %s %s%s%s;",table, ocm.name,ocm.type, ocm.nullable?"":" not null",ocm.unique?" unique":""));	
				}
				if(!cm.useFK && ocm.useFK) {
					if(ocm.useFK && ocm.refTable !=null){
						sql.append(String.format("ALTER TABLE %s ADD CONSTRAINT fk_%s FOREIGN KEY (%s) REFERENCES %s(%s);",table,ocm.name,ocm.name,ocm.refTable,ocm.name));
					}
				}
			}
		}
		Set<String> old= new HashSet<String>();
		for(String[] tuple:uniqueConstraint) {
			List<String> temp = Arrays.asList(tuple);
			old.add(String.join(",", temp));
		}
		for(String[] tuple:other.uniqueConstraint) {
			List<String> temp = Arrays.asList(tuple);
			if(!old.contains(String.join(",", temp))){
				sql.append(String.format("CONSTRAINT %s_%s UNIQUE (%s)",table,String.join("_", tuple),String.join(",", tuple)));		
			}
		}
		return sql.length() == 0 ? null:sql.toString();
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
