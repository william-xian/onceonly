package io.onceonly.db.meta;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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
import io.onceonly.util.OOAssert;
import io.onceonly.util.OOLog;
import io.onceonly.util.OOReflectUtil;
import io.onceonly.util.OOUtils;

public class TableMeta {
	String table;
	String extend;
	String entity;
	ConstraintMeta primaryKey;
	transient List<ConstraintMeta> fieldConstraint = new ArrayList<>(0);
	List<ConstraintMeta> constraints;
	List<ColumnMeta> columnMetas = new ArrayList<>(0);
	transient Map<String,ColumnMeta> nameToColumnMeta = new HashMap<>();
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
		freshConstraintMetaTable();
	}
	public String getEntity() {
		return entity;
	}
	public void setEntity(String entity) {
		this.entity = entity;
	}
	public String getExtend() {
		return extend;
	}
	public void setExtend(String extend) {
		this.extend = extend;
	}
	public List<ColumnMeta> getColumnMetas() {
		return columnMetas;
	}
	public ConstraintMeta getPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(ConstraintMeta primaryKey) {
		this.primaryKey = primaryKey;
	}
	public void setPrimaryKey(String primaryKey) {
		ConstraintMeta pk = new ConstraintMeta();
		pk.setTable(this.table);
		pk.setName(String.format("pk_%s_%s", pk.table,primaryKey));
		pk.setColumns(Arrays.asList(primaryKey));
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
	public ColumnMeta getColumnMetaByName(String colName) {
		return nameToColumnMeta.get(colName);
	}
	public void setColumnMetas(List<ColumnMeta> columnMetas) {
		this.columnMetas = columnMetas;
		this.nameToColumnMeta = new HashMap<>(columnMetas.size());
		this.fieldConstraint = new ArrayList<>(columnMetas.size());
		for(ColumnMeta cm:columnMetas) {
			this.nameToColumnMeta.put(cm.name, cm);
		}
	}
	
	public void freshNameToField() {
		try {
			Class<?> tblEntity = this.getClass().getClassLoader().loadClass(entity);
			List<Class<?>> classes = new ArrayList<>();
			for(Class<?> clazz = tblEntity;!clazz.equals(Object.class);clazz=clazz.getSuperclass()) {
				classes.add(0, clazz);
			}
			Set<String> missed = new HashSet<>(nameToColumnMeta.keySet());
			
			for(Class<?> clazz:classes) {
				for(Field field:clazz.getDeclaredFields()){
					ColumnMeta cm = nameToColumnMeta.get(field.getName());
					if(cm != null){
						field.setAccessible(true);
						cm.setField(field);
						if(field.getType().equals(field.getGenericType())) {
							cm.setJavaBaseType(field.getType());
						}else {
							Class<?> jbt = OOReflectUtil.searchGenType(clazz, classes.get(classes.size()-1), field.getGenericType());
							cm.setJavaBaseType(jbt);
						}
						missed.remove(field.getName());
					}
				}	
			}
			if(!missed.isEmpty()) {
				OOLog.warnning("以下字段没有加载到Field %s", OOUtils.toJSON(missed));
			}
		} catch (ClassNotFoundException e) {
			OOAssert.fatal("无法加载 %s", entity);
		}
	}
	public void freshConstraintMetaTable() {
		if(columnMetas != null && !columnMetas.isEmpty()) {
			nameToColumnMeta.clear();
			fieldConstraint = new ArrayList<>(columnMetas.size());
			for(ColumnMeta cm:columnMetas) {
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
				nameToColumnMeta.put(cm.getName(), cm);
			}
		}
	}
	
	
	private List<String> alterColumnSql(List<ColumnMeta> columnMetas) {
		List<String> sqls = new ArrayList<>();
		for(ColumnMeta ocm:columnMetas) {
			sqls.add(String.format("ALTER TABLE %s ALTER %s %s%s;",table, ocm.name,ocm.type, ocm.nullable?"":" not null"));	
		}
		return sqls;
	}
	private List<String> addColumnSql(List<ColumnMeta> columnMetas) {
		List<String> sqls = new ArrayList<>();
		for(ColumnMeta ocm:columnMetas) {
			sqls.add(String.format("ALTER TABLE %s ADD %s %s%s;",table, ocm.name,ocm.type, ocm.nullable?"":" not null"));	
		}
		return sqls;
	}

	
	/** drop table if exists tbl_a;*/
	public List<String> createTableSql() {
		List<String> sqls = new ArrayList<>();
		StringBuffer tbl = new StringBuffer();
		tbl.append(String.format("CREATE TABLE %s (", table));
		for(ColumnMeta cm:columnMetas) {
			tbl.append(String.format("%s %s%s,", cm.name,cm.type, cm.nullable?"":" not null"));
		}
		tbl.delete(tbl.length()-1, tbl.length());
		tbl.append(");");
		sqls.add(tbl.toString());
		if(primaryKey != null) {
			sqls.add(primaryKey.addSql());
		}
		/** 添加复合约束 */
		sqls.addAll(ConstraintMeta.addConstraintSql(fieldConstraint));
		return sqls;
	}

	/**
	 * 升级数据库，返回需要执行的sql
	 * @param other
	 * @return
	 */
	public List<String> upgradeTo(TableMeta other) {
		if(!table.equals(other.table)) {
			return null;
		}
		List<String> sqls = new ArrayList<>();
		List<ColumnMeta> otherColumn = other.columnMetas;
		List<ColumnMeta> newColumns = new ArrayList<>();
		List<ConstraintMeta> dropIndexs = new ArrayList<>();
		List<ConstraintMeta> dropForeignKeys = new ArrayList<>();
		List<ColumnMeta> alterColumns = new ArrayList<>();
		List<ConstraintMeta> addForeignKeys = new ArrayList<>();
		for(ColumnMeta ocm:otherColumn) {
			ColumnMeta cm = nameToColumnMeta.get(ocm.name);
			System.err.println(" cm:" + OOUtils.toJSON(cm));
			System.err.println("ocm:" + OOUtils.toJSON(ocm));
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
		if(primaryKey != null && !primaryKey.equals(other.primaryKey)) {
			sqls.add(primaryKey.dropSql());
		}
		if(other.primaryKey != null && !other.primaryKey.equals(primaryKey)) {
			sqls.add(other.primaryKey.addSql());
		}
		sqls.addAll(addColumnSql(newColumns));
		
		sqls.addAll(ConstraintMeta.dropConstraintSql(dropIndexs));
		sqls.addAll(ConstraintMeta.dropConstraintSql(dropForeignKeys));
		sqls.addAll(alterColumnSql(alterColumns));
		sqls.addAll(ConstraintMeta.addConstraintSql(addForeignKeys));
		sqls.addAll(ConstraintMeta.dropConstraintSql(dropUniqueConstraint));
		sqls.addAll(ConstraintMeta.addConstraintSql(addUniqueConstraint));
		return sqls;
	}
	public static TableMeta createBy(Class<?> entity) {
		Tbl tbl = entity.getAnnotation(Tbl.class);
		if(tbl == null) {
			return null;
		}
		TableMeta tm = new TableMeta();
		tm.table = entity.getSimpleName();
		tm.entity = entity.getName();
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
		List<Class<?>> classes = new ArrayList<>();
		for(Class<?> clazz = entity;!clazz.equals(Object.class);clazz=clazz.getSuperclass()) {
			classes.add(0, clazz);
		}
		for (Class<?> clazz : classes) {
			for (Field field : clazz.getDeclaredFields()) {
				Col col = field.getAnnotation(Col.class);
				if (col == null) {
					continue;
				}
				ColumnMeta cm = new ColumnMeta();
				cm.setName(field.getName());
				OId oid = field.getAnnotation(OId.class);
				if (oid != null) {
					primaryKeys.add(field.getName());
					cm.setPrimaryKey(true);
				}
				cm.setNullable(col.nullable());
				cm.setPattern(col.pattern());
				if (col.colDef().equals("")) {
					Class<?> javaBaseType = cm.getJavaBaseType();
					if(javaBaseType == null) {
						if(field.getType() == Object.class) {
							javaBaseType = OOReflectUtil.searchGenType(clazz, classes.get(classes.size()-1), field.getGenericType());
							cm.setJavaBaseType(javaBaseType);
						}else {
							javaBaseType = field.getType();
							cm.setJavaBaseType(javaBaseType);
						}
					}
					String type = transType(clazz,entity,javaBaseType, col);
					cm.setType(type);
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
		if(primaryKeys.size() > 1) {
			OOAssert.fatal("不支持符合主键 %s(%s)", tm.table,String.join(",", primaryKeys));
		}
		tm.setPrimaryKey(primaryKeys.get(0));
		tm.freshNameToField();
		tm.freshConstraintMetaTable();
		return tm;
	}
	/**  
	 * 以postgresql為准 */
	private static String transType(Class<?> forefather,Class<?> clazz,Class<?> type,Col col) {
		if(type.equals(Long.class) || type.equals(long.class)) {
			return "bigint";
		}else if(type.equals(String.class)) {
			return String.format("varchar(%d)", col.size());	
		}else if(type.equals(Integer.class) || type.equals(int.class)) {
			return "integer";	
		}else if(type.equals(BigDecimal.class)) {
			return String.format("decimal(%d,%d)", col.precision(), col.scale());
		}else if(type.equals(Boolean.class) || type.equals(boolean.class)) {
			return "boolean";	
		}else if(type.equals(Short.class) || type.equals(short.class)) {
			return "smallint";	
		}else if(type.equals(Float.class) || type.equals(float.class)) {
			return "float";
		}else if(type.equals(Double.class) || type.equals(double.class)) {
			return "double precision";
		}else {
			OOAssert.fatal("不支持的数据类型:%s", type);
		}
		return null;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((columnMetas == null) ? 0 : columnMetas.hashCode());
		result = prime * result + ((constraints == null) ? 0 : constraints.hashCode());
		result = prime * result + ((entity == null) ? 0 : entity.hashCode());
		result = prime * result + ((extend == null) ? 0 : extend.hashCode());
		result = prime * result + ((fieldConstraint == null) ? 0 : fieldConstraint.hashCode());
		result = prime * result + ((primaryKey == null) ? 0 : primaryKey.hashCode());
		result = prime * result + ((table == null) ? 0 : table.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TableMeta other = (TableMeta) obj;
		if (columnMetas == null) {
			if (other.columnMetas != null)
				return false;
		} else if (!columnMetas.equals(other.columnMetas))
			return false;
		if (constraints == null) {
			if (other.constraints != null)
				return false;
		} else if (!constraints.equals(other.constraints))
			return false;
		if (entity == null) {
			if (other.entity != null)
				return false;
		} else if (!entity.equals(other.entity))
			return false;
		if (extend == null) {
			if (other.extend != null)
				return false;
		} else if (!extend.equals(other.extend))
			return false;
		if (fieldConstraint == null) {
			if (other.fieldConstraint != null)
				return false;
		} else if (!fieldConstraint.equals(other.fieldConstraint))
			return false;
		if (primaryKey == null) {
			if (other.primaryKey != null)
				return false;
		} else if (!primaryKey.equals(other.primaryKey))
			return false;
		if (table == null) {
			if (other.table != null)
				return false;
		} else if (!table.equals(other.table))
			return false;
		return true;
	}
	
}

