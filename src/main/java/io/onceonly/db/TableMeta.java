package io.onceonly.db;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.onceonly.db.annotation.Col;
import io.onceonly.db.annotation.Constraint;
import io.onceonly.db.annotation.OId;
import io.onceonly.db.annotation.Tbl;

public class TableMeta {
	String table;
	/** 复合主键以逗号隔开不可有空格*/
	String primaryKey;
	List<String[]> uniqueConstraint;
	List<ConstraintMeta> constraints;
	Map<String,ColumnMeta> columnMeta;
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public String getPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}
	public List<ConstraintMeta> getConstraints() {
		return constraints;
	}
	public void setConstraints(List<ConstraintMeta> constraints) {
		this.constraints = constraints;
	}
	public Map<String, ColumnMeta> getColumnMeta() {
		return columnMeta;
	}
	public void setColumnMetas(Collection<ColumnMeta> columnMetas) {
		this.columnMeta = new HashMap<>(columnMetas.size());
		for(ColumnMeta cm:columnMetas) {
			this.columnMeta.put(cm.name, cm);
		}
	}
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
	public static TableMeta createBy(Class<?> entity) {
		Tbl tbl = entity.getAnnotation(Tbl.class);
		if(tbl == null) {
			return null;
		}
		TableMeta tm = new TableMeta();
		tm.table = entity.getSimpleName();
		List<ConstraintMeta> constraints =  new ArrayList<>();
		for(Constraint c:tbl.constraints()) {
			ConstraintMeta cm = new ConstraintMeta();
			constraints.add(cm);
			cm.setColumns(Arrays.asList(c.colNames()));
			cm.setTable(tm.getTable());
			cm.setType(c.type());
			cm.setUsing(c.using());
		}
		tm.setConstraints(constraints);
		List<ColumnMeta> columnMetas = new ArrayList<>();
		List<String> primaryKeys = new ArrayList<>();
		for(Class<?> clazz = entity;!clazz.equals(Object.class);clazz=clazz.getSuperclass()) {
		for(Field field:clazz.getDeclaredFields()) {
			Col col = field.getAnnotation(Col.class);
			if (col == null) {
				continue;
			}
			ColumnMeta cm = new ColumnMeta();
			cm.setName(field.getName());
			OId oid = field.getAnnotation(OId.class);
			if(oid != null) {
				primaryKeys.add(field.getName());
			}
			cm.setNullable(col.nullable());
				cm.setPattern(col.pattern());
				if (col.colDef().equals("")) {
					String type = transType(field, col);
					if (type != null) {
						cm.setType(type);
					} else {
						continue;
					}
				} else {
					cm.setType(col.colDef());
				}
				cm.setUnique(col.unique());
				cm.setUsing(col.using());
				if (col.ref() != void.class) {
					cm.setUseFK(col.useFK());
					cm.setRefTable(col.ref().getSimpleName());
					cm.setRefField(col.refField());
				}
	
			columnMetas.add(cm);
		}
		}
		tm.setColumnMetas(columnMetas);
		tm.setPrimaryKey(String.join(",", primaryKeys));
		return tm;
	}
	/** 以postgresql為准 */
	private static String transType(Field field,Col col) {
		if(field.getType().equals(Long.class) || field.getType().equals(long.class)) {
			return "bigint";
		}else if(field.getType().equals(String.class)) {
			return String.format("varchar(%d)", col.size());	
		}else if(field.getType().equals(Integer.class) || field.getType().equals(int.class)) {
			return "integer";	
		}else if(field.getType().equals(BigDecimal.class)) {
			return String.format("decimal(%d,%d)", col.precision(), col.scale());
		}else if(field.getType().equals(Boolean.class) || field.getType().equals(boolean.class)) {
			return "boolean";	
		}else if(field.getType().equals(Short.class) || field.getType().equals(short.class)) {
			return "smallint";	
		}else if(field.getType().equals(Float.class) || field.getType().equals(float.class)) {
			return "float";
		}else if(field.getType().equals(Double.class) || field.getType().equals(double.class)) {
			return "double precision";
		}
		return null;
	}
}

