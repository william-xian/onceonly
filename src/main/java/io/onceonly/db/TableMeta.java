package io.onceonly.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.onceonly.db.annotation.Join;
import io.onceonly.db.annotation.VTable;
import io.onceonly.exception.Failed;
import io.onceonly.util.OOAssert;
import io.onceonly.util.Tuple2;
import io.onceonly.util.Tuple3;

public class TableMeta {
	private Class<?> entity;
	private Set<String> columns = new HashSet<>();
	private VTable vt;
	private Map<String,Class<?>> aliasToEntity = new HashMap<>();
	
	private Map<String,String> columnToOriginal = new HashMap<>();
	/** 表别名 -> (依赖表，依赖条件)*/
	private Map<String,Tuple2<String,String>> depends = new HashMap<>();
	
	public Class<?> getEntity() {
		return entity;
	}
	public void setEntity(Class<?> entity) {
		this.entity = entity;
	}
	
	public VTable getVt() {
		return vt;
	}
	public void setVt(VTable vt) {
		this.vt = vt;
	}
	public Set<String> getColumns() {
		return columns;
	}
	public void setColumns(Set<String> columns) {
		this.columns = columns;
	}
	public Map<String, Class<?>> getAliasToEntity() {
		return aliasToEntity;
	}
	public void setAliasToEntity(Map<String, Class<?>> aliasToEntity) {
		this.aliasToEntity = aliasToEntity;
	}
	public Map<String, String> getColumnToOriginal() {
		return columnToOriginal;
	}
	public void setColumnToOriginal(Map<String, String> columnToOriginal) {
		this.columnToOriginal = columnToOriginal;
	}
	public Map<String, Tuple2<String,String>> getDepends() {
		return depends;
	}
	public void setDepends(Map<String, Tuple2<String,String>> depends) {
		this.depends = depends;
	}
	
	public static String generateJoinSQL(VTable vt, Set<String> classes) {
		StringBuffer sb  = new StringBuffer();
		sb.append(String.format("from %s %s \n", vt.mainTable().getSimpleName(),vt.alias()));
		for(Join join:vt.joins()) {
			String name = "";
			if(!join.alias().equals("")) {
				name = join.alias();
			} else {
				if(join.left() != void.class) {
					name = join.left().getSimpleName();
				}else if(join.right() != void.class) {
					name = join.right().getSimpleName();
				}else {
					OOAssert.fatal("没有别名引用类，也没有实体类，错误的配置");
					continue;
				}
			}
			if(classes != null && !classes.contains(name)) {
				continue;
			}
			
			String sql = null;
			String originTbl = join.alias();
			String targetTbl = join.tAlias();
			if(join.tAlias().equals("")) {
				if(join.target() != void.class) {
					targetTbl = join.target().getSimpleName();
				}else {
					if(!vt.alias().equals("")) {
						targetTbl = vt.alias();
					}else {
						targetTbl = vt.mainTable().getSimpleName();
					}
				}
			}
			if(join.left() != void.class) {
				if(join.alias().equals("")) {
					originTbl = join.left().getSimpleName();
				}
				if(!join.cnd().equals("")) {
					sql = String.format("left join %s %s on %s", 
							join.left().getSimpleName(), join.alias(),join.cnd());
				}else {
					sql = String.format("left join %s %s on %s.id = %s.id and %s.del=false", 
							join.left().getSimpleName(), join.alias(),originTbl,targetTbl,originTbl);
				}
			}else if(join.right() != void.class) {
				if(join.alias().equals("")) {
					originTbl = join.right().getSimpleName();
				}
				if(!join.cnd().equals("")) {
					sql = String.format("right join %s %s on %s", 
							join.right().getSimpleName(), join.alias(),join.cnd());
				}else {
					sql = String.format("right join %s %s on %s.id = %s.id and %s.del=false", 
							join.right().getSimpleName(), join.alias(),originTbl,targetTbl,originTbl);
				}
			}else {
				OOAssert.fatal("没有别名引用类，也没有实体类，错误的配置");
			}
			sb.append(sql+"\n");
		}
		return sb.toString();
	}
	
	public SqlTask generateSqlByParam(Set<String> params) {
		Set<String> classes = generateDenpendTableByParams(params);
		classes.add(vt.alias());
		String joinSQL = generateJoinSQL(vt,classes);
		if(columnToOriginal.isEmpty()) {
			OOAssert.fatal("没有字段的表");
		}
		Map<String,Set<String>> aliasToMissColumns = new HashMap<>();
		StringBuffer sb = new StringBuffer("select ");
		boolean hasBefore = false;
		for(String column:columnToOriginal.keySet()) {
			String original = columnToOriginal.get(column);
			String alias = original.split("\\.")[0];
			if(classes.contains(alias)) {
				if(hasBefore) {
					sb.append(", " + original + " " + column);
				}else {
					sb.append(original + " " + column);	
				}
				hasBefore = true;
			}else {
				Set<String> columns = aliasToMissColumns.get(alias);
				if(columns == null) {
					columns = new HashSet<>();
					aliasToMissColumns.put(alias, columns);
				}
				columns.add(column);
			}
		}
		sb.append("\n" + joinSQL);
		
		Set<String> missTableAlias = new HashSet<>(aliasToEntity.keySet());
		missTableAlias.removeAll(classes);
		
		
		List<Tuple3<Class<?>,String,Class<?>>> task = new ArrayList<>();
		for(String alias:missTableAlias) {
			Tuple2<String,String> depend = depends.get(alias);
			String last = alias;
			while(depend != null) {
				Class<?> a = aliasToEntity.get(depend.a);
				Class<?> b = aliasToEntity.get(last);
				String cnd = depend.b.replaceAll(depend.a+"\\.", a.getSimpleName()+".");
				cnd = cnd.replaceAll(last+"\\.", b.getSimpleName()+".");
				Tuple3<Class<?>,String,Class<?>> tuple = new Tuple3<>(a,cnd,b);
				task.add(tuple);
				last = depend.a;
				depend = depends.get(depend.a);
			}
			Collections.reverse(task);
		}
		
		Map<Class<?>, Set<String>> missColumns = new HashMap<>(aliasToMissColumns.size());
		for(String alias:aliasToMissColumns.keySet()) {
			Class<?> key = aliasToEntity.get(alias);
			Set<String> columnSet = missColumns.get(key);
			if(columnSet != null) {
				columnSet.addAll(aliasToMissColumns.get(alias));
			}else {
				missColumns.put(key, aliasToMissColumns.get(alias));
			}
		}
		
		SqlTask st = new SqlTask();
		st.setJoinSql(sb.toString());
		st.setTask(task);
		st.setMissColumns(missColumns);
		return st;
	}

	/**
	 * 
	 * @param tableToEntity
	 * @param columnToOrigin
	 * @param denpends
	 * @param params
	 * @return
	 */
	private Set<String> generateDenpendTableByParams(Set<String> params) {
		Set<String> set = new HashSet<>();		
		if(params == null || params.isEmpty()) {
			return set;
		}
		for(String column:params) {
			String orignal = columnToOriginal.get(column);
			if(orignal.matches(".*\\(.*\\).*")) {
				String oc = orignal.replaceAll(".*\\((.*)\\).*", "$1");
				if(oc.matches("[a-zA-Z0-9_]{1,}\\.[a-zA-Z0-9_]{1,}")) {
					set.add(oc.split("\\.")[0]);
				}
			}else if(orignal.matches("[a-zA-Z0-9_]{1,}\\.[a-zA-Z0-9_]{1,}")) {
				set.add(orignal.split("\\.")[0]);
			}
			
		}
		Set<String> result = new HashSet<>();
		for(String alias:set) {
			if(!aliasToEntity.containsKey(alias)) {
				Failed.throwError("Entity alias:%s is not exist!", alias);
			}
			result.add(alias);
			Tuple2<String,String> depend = depends.get(alias);
			while(depend != null) { 
				result.add(depend.a);
				depend = depends.get(depend.a);
			}
		}
		return result;
	}
	
	
}
