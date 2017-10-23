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
import io.onceonly.db.annotation.ConstraintType;
import io.onceonly.db.annotation.OId;
import io.onceonly.db.annotation.Tbl;

public class TableMeta {
	String table;
	ConstraintMeta primaryKey;
	List<ConstraintMeta> fieldConstraint = new ArrayList<>(0);
	List<ConstraintMeta> constraints;
	Map<String,ColumnMeta> columnMeta = new HashMap<>();
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
		freshConstraintMetaTable();
	}
	
	public ConstraintMeta getPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(ConstraintMeta primaryKey) {
		this.primaryKey = primaryKey;
	}
	public void setPrimaryKey(List<String> primaryKeys) {
		ConstraintMeta pk = new ConstraintMeta();
		pk.setTable(this.table);
		pk.setName(String.format("pk_%s_%s", pk.table,String.join("_", primaryKeys)));
		pk.setColumns(primaryKeys);
		pk.setType(ConstraintType.PRIMARY_KEY);
		pk.setUsing("BTREE");
		this.primaryKey = pk;
	}
	
	public List<ConstraintMeta> getFieldConstraint() {
		return fieldConstraint;
	}
	public void setFieldConstraint(List<ConstraintMeta> fieldConstraint) {
		this.fieldConstraint = fieldConstraint;
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
		this.fieldConstraint = new ArrayList<>(columnMetas.size());
		for(ColumnMeta cm:columnMetas) {
			this.columnMeta.put(cm.name, cm);
		}
		freshConstraintMetaTable();
	}
	
	private void freshConstraintMetaTable() {
		if(columnMeta != null && !columnMeta.isEmpty()) {
			fieldConstraint = new ArrayList<>(columnMeta.size());
			for(ColumnMeta cm:columnMeta.values()) {
				if(cm.unique){
					ConstraintMeta cnsMeta = new ConstraintMeta();
					List<String> cols = new ArrayList<> ();
					cols.add(cm.getName());
					cnsMeta.setColumns(new ArrayList<String>(cols));
					cnsMeta.setTable(this.getTable());
					cnsMeta.setName("un_" + cnsMeta.getTable()+"_"+cm.name);
					cnsMeta.setUsing(cm.using);
					cnsMeta.setType(ConstraintType.UNIQUE);
					fieldConstraint.add(cnsMeta);
				}else if(cm.useFK && cm.refTable != null) {
					ConstraintMeta cnsMeta = new ConstraintMeta();
					List<String> cols = new ArrayList<> ();
					cols.add(cm.getName());
					cnsMeta.setColumns(new ArrayList<String>(cols));
					cnsMeta.setTable(this.getTable());
					cnsMeta.setName("fk_"+cnsMeta.getTable()+"_"+cm.name);
					cnsMeta.setUsing(cm.using);
					cnsMeta.setType(ConstraintType.FOREGIN_KEY);
					cnsMeta.setRefTable(cm.refTable);
					fieldConstraint.add(cnsMeta);
				}
			}
		}
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
			tbl.append(primaryKey.genSql(ConstraintOpt.ADD));
		}
		/** 添加复合约束 */
		tbl.append(ConstraintMeta.addConstraintSql(fieldConstraint));
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
		List<ConstraintMeta> dropIndexs = new ArrayList<>();
		List<ConstraintMeta> dropForeignKeys = new ArrayList<>();
		List<ColumnMeta> alterColumns = new ArrayList<>();
		List<ConstraintMeta> addForeignKeys = new ArrayList<>();
		for(ColumnMeta ocm:otherColumn.values()) {
			ColumnMeta cm = columnMeta.get(ocm.name);
			if(cm == null) {				
				newColumns.add(ocm);
			}else {
				if(cm.unique &&!ocm.unique) {
					ConstraintMeta cnstMeta = new ConstraintMeta();
					cnstMeta.setColumns(Arrays.asList(ocm.getName()));
					cnstMeta.setTable(table);
					cnstMeta.setType(ConstraintType.UNIQUE);
					cnstMeta.setUsing(ocm.getUsing());
					dropIndexs.add(cnstMeta);
				}
				/** 删除外键  */
				if(cm.useFK && !ocm.useFK) {
					ConstraintMeta cnstMeta = new ConstraintMeta();
					cnstMeta.setColumns(Arrays.asList(cm.getName()));
					cnstMeta.setTable(table);
					cnstMeta.setType(ConstraintType.FOREGIN_KEY);
					cnstMeta.setRefTable(cm.getRefField());
					cnstMeta.setUsing(cm.getUsing());
					dropForeignKeys.add(cnstMeta);
				}
				if(!cm.type.equals(ocm.type) || cm.nullable != ocm.nullable) {
					alterColumns.add(ocm);
				}
				if(!cm.useFK && ocm.useFK) {
					if(ocm.useFK && ocm.refTable !=null){
						ConstraintMeta cnstMeta = new ConstraintMeta();
						cnstMeta.setColumns(Arrays.asList(cm.getName()));
						cnstMeta.setTable(table);
						cnstMeta.setType(ConstraintType.FOREGIN_KEY);
						cnstMeta.setRefTable(cm.getRefField());
						cnstMeta.setUsing(cm.getUsing());
						addForeignKeys.add(cnstMeta);
					}
				}
			}
		}
		
		Set<String> oldConstraintSet = new HashSet<String>();
		Set<String> currentSet = new HashSet<String>();
		
		for(ConstraintMeta tuple:fieldConstraint) {
			oldConstraintSet.add(String.join(",", tuple.columns));
		}
		List<ConstraintMeta> addUniqueConstraint = new ArrayList<>();
		for(ConstraintMeta tuple:other.fieldConstraint) {
			currentSet.add(String.join(",", tuple.columns));
			if(!oldConstraintSet.contains(String.join(",", tuple.columns))){
				addUniqueConstraint.add(tuple);
			}
		}
		List<ConstraintMeta> dropUniqueConstraint = new ArrayList<>();
		for(ConstraintMeta tuple:fieldConstraint) {
			if(!currentSet.contains(String.join(",", tuple.columns))){
				dropUniqueConstraint.add(tuple);
			}
		}
		if(primaryKey != null && primaryKey.equals(other.primaryKey)) {
			sql.append(primaryKey.genSql(ConstraintOpt.DROP));
		}
		if(other.primaryKey != null && other.primaryKey.equals(primaryKey)) {
			sql.append(other.primaryKey.genSql(ConstraintOpt.ADD));
		}
		sql.append(addColumnSql(newColumns));
		
		sql.append(ConstraintMeta.dropConstraintSql(dropIndexs));
		sql.append(ConstraintMeta.dropConstraintSql(dropForeignKeys));
		sql.append(alterColumnSql(alterColumns));
		sql.append(ConstraintMeta.addConstraintSql(addForeignKeys));
		sql.append(ConstraintMeta.dropConstraintSql(dropUniqueConstraint));
		sql.append(ConstraintMeta.addConstraintSql(addUniqueConstraint));
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
		tm.setPrimaryKey(primaryKeys);
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

