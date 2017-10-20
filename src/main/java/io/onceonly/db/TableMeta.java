package io.onceonly.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TableMeta {
	String table;
	/** 复合主键以逗号隔开不可有空格*/
	String primaryKey;
	List<String[]> uniqueConstraint;
	List<ConstraintMeta> constraints;
	Map<String,ColumnMeta> columnMeta;
	
	private String addPrimaryKeySql(String primaryKey) {
		StringBuffer sql = new StringBuffer();
		sql.append(String.format("ALTER TABLE %s DROP CONSTRAINT %s;",table,primaryKey.replace(',', '_')));
		sql.append(String.format("ALTER TABLE %s ADD CONSTRAINT %s PRIMARY KEY (%s);",table,primaryKey.replace(',', '_'),primaryKey));
		return sql.toString();
	}
	private String dropPrimaryKeySql(String primaryKey) {
		StringBuffer sql = new StringBuffer();
		sql.append(String.format("ALTER TABLE %s DROP CONSTRAINT %s;",table,primaryKey.replace(',', '_')));
		return sql.toString();
	}
	private String addUniqueSql(Collection<ColumnMeta> columnMetas) {
		StringBuffer sql = new StringBuffer();
		for(ColumnMeta ocm:columnMetas) {
			if(ocm.unique){
				sql.append(String.format("ALTER TABLE %s ADD CONSTRAINT %s_%s UNIQUE (%s);",table,table,ocm.name));	
			}	
		}
		return sql.toString();
	}
	private String dropUniqueSql(Collection<ColumnMeta> columnMetas) {
		StringBuffer sql = new StringBuffer();
		for(ColumnMeta ocm:columnMetas) {
			sql.append(String.format("ALTER TABLE %s DROP CONSTRAINT %s_%s;",table, table, ocm.name));
		}
		return sql.toString();
	}
	private String addForeignKeySql(Collection<ColumnMeta> columnMetas) {
		StringBuffer sql = new StringBuffer();
		for(ColumnMeta ocm:columnMetas) {
			if(ocm.useFK && ocm.refTable !=null){
				sql.append(String.format("ALTER TABLE %s ADD CONSTRAINT %s_%s FOREIGN KEY (%s) REFERENCES %s(%s);",table,table,ocm.name,ocm.name,ocm.refTable,ocm.name));
			}	
		}
		return sql.toString();
	}
	private String dropForeignKeySql(List<ColumnMeta> columnMetas) {
		StringBuffer sql = new StringBuffer();
		for(ColumnMeta ocm:columnMetas) {
			sql.append(String.format("ALTER TABLE %s DROP CONSTRAINT %s_%s FOREIGN KEY (%s);",table,table,ocm.name,ocm.name));
		}
		return sql.toString();
	}

	private String alterColumnSql(List<ColumnMeta> columnMetas) {
		StringBuffer sql = new StringBuffer();
		for(ColumnMeta ocm:columnMetas) {
			sql.append(String.format("ALTER TABLE %s ALTER %s %s%s;",table, ocm.name,ocm.type, ocm.nullable?"":" not null"));	
		}
		return sql.toString();
	}
	private String addColumnSql(List<ColumnMeta> columnMetas) {
		StringBuffer sql = new StringBuffer();
		for(ColumnMeta ocm:columnMetas) {
			sql.append(String.format("ALTER TABLE %s ADD %s %s%s;",table, ocm.name,ocm.type, ocm.nullable?"":" not null"));	
		}
		return sql.toString();
	}

	private String addConstraintSql(List<String[]> uniqueConstraint) {
		StringBuffer sql = new StringBuffer();
		for(String[] tuple:uniqueConstraint) {
			sql.append(String.format("ALTER TABLE %s ADD CONSTRAINT %s_%s UNIQUE (%s);",table,table,String.join("_", tuple),String.join(",", tuple)));		
		}
		return sql.toString();
	}
	private String dropConstraintSql(List<String[]> uniqueConstraint) {
		StringBuffer sql = new StringBuffer();
		for(String[] tuple:uniqueConstraint) {
			sql.append(String.format("ALTER TABLE %s DROP CONSTRAINT %s_%s;",table,table,String.join("_", tuple)));		
		}
		return sql.toString();
	}
	
	/** drop table if exists tbl_a;*/
	public String createTableSql() {
		StringBuffer tbl = new StringBuffer();
		tbl.append(String.format("CREATE TABLE %s (", table));
		for(ColumnMeta cm:columnMeta.values()) {
			tbl.append(String.format("%s %s%s,", cm.name,cm.type, cm.nullable?"":" not null"));
		}
		tbl.delete(tbl.length()-1, tbl.length());
		tbl.append(");");
		if(primaryKey != null) {
			tbl.append(addPrimaryKeySql(primaryKey));
		}
		/** 添加外键 */
		tbl.append(addForeignKeySql(this.columnMeta.values()));
		/** 添加唯一约束 */
		tbl.append(addUniqueSql(this.columnMeta.values()));
		/** 添加复合约束 */
		tbl.append(addConstraintSql(uniqueConstraint));
		return tbl.toString();
	}

	/**
	 * 升级数据库，返回需要执行的sql
	 * @param other
	 * @return
	 */
	public String upgradeTo(TableMeta other) {
		if(!table.equals(other.table)) {
			return null;
		}
		StringBuffer sql = new StringBuffer();
		Map<String,ColumnMeta> otherColumn = other.columnMeta;
		List<ColumnMeta> newColumns = new ArrayList<>();
		List<ColumnMeta> dropIndexs = new ArrayList<>();
		List<ColumnMeta> dropForeignKeys = new ArrayList<>();
		List<ColumnMeta> alterColumns = new ArrayList<>();
		List<ColumnMeta> addForeignKeys = new ArrayList<>();
		for(ColumnMeta ocm:otherColumn.values()) {
			ColumnMeta cm = columnMeta.get(ocm.name);
			if(cm == null) {
				newColumns.add(ocm);
			}else {
				if(cm.unique &&!ocm.unique) {
					dropIndexs.add(ocm);
				}
				/** 删除外键  */
				if(cm.useFK && !ocm.useFK) {
					dropForeignKeys.add(cm);
				}
				if(!cm.type.equals(ocm.type) || cm.nullable != ocm.nullable) {
					alterColumns.add(ocm);
				}
				if(!cm.useFK && ocm.useFK) {
					if(ocm.useFK && ocm.refTable !=null){
						addForeignKeys.add(ocm);
					}
				}
			}
		}
		Set<String> oldConstraintSet = new HashSet<String>();
		Set<String> currentSet = new HashSet<String>();
		for(String[] tuple:uniqueConstraint) {
			oldConstraintSet.add(String.join(",", tuple));
		}
		List<String[]> addUniqueConstraint = new ArrayList<>();
		for(String[] tuple:other.uniqueConstraint) {
			currentSet.add(String.join(",", tuple));
			if(!oldConstraintSet.contains(String.join(",", tuple))){
				addUniqueConstraint.add(tuple);
			}
		}
		List<String[]> dropUniqueConstraint = new ArrayList<>();
		for(String[] tuple:uniqueConstraint) {
			if(!currentSet.contains(String.join(",", tuple))){
				dropUniqueConstraint.add(tuple);
			}
		}
		
		if(primaryKey != null && primaryKey.equals(other.primaryKey)) {
			sql.append(dropPrimaryKeySql(primaryKey));
		}
		if(other.primaryKey != null && other.primaryKey.equals(primaryKey)) {
			sql.append(addPrimaryKeySql(other.primaryKey));
		}
		sql.append(addColumnSql(newColumns));
		sql.append(dropUniqueSql(dropIndexs));
		sql.append(dropForeignKeySql(dropForeignKeys));
		sql.append(alterColumnSql(alterColumns));
		sql.append(addForeignKeySql(addForeignKeys));
		sql.append(dropConstraintSql(dropUniqueConstraint));
		sql.append(addConstraintSql(addUniqueConstraint));
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
	String using;
	/**
	 * 正则表达式 或@JSON,@Email等特定教研
	 */
	String pattern;
}


class ConstraintMeta {
	String name;
	static final String PRIMARY_KEY = "PRIMARY KEY";
	static final String FOREIGN_KEY = "FOREIGN KEY";
	static final String UNIQUE = "UNIQUE";
	static final String INDEX = "INDEX";
	String type;
	String using;
	String table;
	String refTable;
	List<String> columns;
}

/* TODO
ALTER TABLE Persons	ADD CONSTRAINT pk_PersonID PRIMARY KEY (Id_P,LastName)
ALTER TABLE Orders  ADD CONSTRAINT fk_PerOrders FOREIGN KEY (Id_P) REFERENCES Persons(Id_P);
ALTER TABLE Persons ADD CONSTRAINT uc_PersonID UNIQUE (Id_P,LastName)
CREATE INDEX index_name ON table_name (column_name)
CREATE INDEX name ON table USING HASH (column);
CREATE UNIQUE INDEX name ON table (column [, ...]);
ALTER TABLE Persons DROP CONSTRAINT pk_PersonID
ALTER TABLE Persons DROP CONSTRAINT uc_PersonID
ALTER TABLE table_name DROP INDEX index_name
*/
class AlterSqlTask {
	int order;
	String opt;
	ConstraintMeta cm;
	String toSql() {
		return null;
	}
}
