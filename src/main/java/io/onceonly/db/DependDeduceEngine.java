package io.onceonly.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.onceonly.util.OOAssert;
import io.onceonly.util.OOUtils;

public class DependDeduceEngine {
	
	public Map<String,DDMeta> pathToMeta= new HashMap<>();
	
	/* ------------------------------------- */
	public Map<String,DDMeta> aliasToMeta= new HashMap<>();
	/**
	 * 虚表字段，表来源
	 */
	public Map<String,DDMeta> columnToMeta =  new HashMap<>();
	/**
	 * <表，<关联表，关联关系>>
	 */
	public	Map<String,Map<String,String>> relMap= new HashMap<>();
	
	/**
	 * 添加推导关系和结果集 <b>此处的关系是一一对应的</b>
	 * @param result   A {id aid,name AName,bid}; A.bid-B {name BName,CId}; A.bid-B.cid-C {name CName}
	 * @return
	 */
	public DependDeduceEngine append(String resultSet) {
		String[] sets = resultSet.split(";");
		for(String set:sets) {
			String[] tbl_columns = set.split("\\{|\\}");
			if(tbl_columns.length >=2) {
				String path = tbl_columns[0].trim();
				String[] columns = tbl_columns[1].split(",");
				DDMeta meta = new DDMeta();
				meta.setPath(path);
				meta.setColumnMapping(Arrays.asList(columns));
				pathToMeta.put(path, meta);
			}else {
				OOAssert.warnning("%s -> %s 不合法", resultSet, set);
			}
		}
		return this;
	}
	
	private String tranAlias(int id) {
		return "T"+id;
	}
	
	public void build() {
		supplyDDMeta();
		
		List<String> paths = new ArrayList<>(pathToMeta.keySet());
		Collections.sort(paths);
		int id = 0;
		for(String path:paths) {
			DDMeta meta = pathToMeta.get(path);
			meta.setName(tranAlias(id));
			id++;
		}
		for(String path:paths) {
			DDMeta meta = pathToMeta.get(path);
			resoveDDMeta(meta);
		}
		
		/** 生成对应关系  */
		for(DDMeta meta:pathToMeta.values()) {
			aliasToMeta.put(meta.getName(), meta);
			for(String col:meta.getColumns()) {
				columnToMeta.put(col, meta);	
			}
		}
	}
	/** 
	 * 补充中间过程表
	 */
	private void supplyDDMeta() {
		List<String> keys = new ArrayList<>(pathToMeta.keySet());
		Collections.sort(keys);
		for(String path:keys) {
			String[] pathArr = path.split("-");
			String lastTbl = null;
			for(int i = 0; i < pathArr.length; i++) {
				String curTbl = pathArr[i];
				String tbl = curTbl.replaceAll("\\.>*", "");
				if(tbl.equals(lastTbl)) {
					List<String> plist = new ArrayList<>();
					for(int t = 0; t < i; t++) {
						plist.add(pathArr[t]);
					}
					plist.add(tbl);
					String npath = String.join("-", plist);
					if(!pathToMeta.containsKey(npath)) {
						DDMeta  nmeta = new DDMeta();
						nmeta.setPath(npath);
						pathToMeta.put(npath, nmeta);	
					}
				}
				lastTbl = tbl;
			}
		}
	}
	private String getPathTable(String path) {
		int indexTbl = path.lastIndexOf('.');
		if(indexTbl < 0) {
			return path;
		}else {
			return path.substring(0,indexTbl);
		}
	}
	private void resoveDDMeta(DDMeta meta) {
		String path = meta.getPath();
		int sep = path.lastIndexOf('-');
		if(sep < 0) {
			/** 没有分割符，说明是命名根  */
			meta.setTable(path);
			return;
		}
		String a = path.substring(0, sep);
		String b = path.substring(sep+1);
		meta.setTable(b.replaceAll("\\..*", ""));
		
		DDMeta metaA = pathToMeta.get(a);
		if(metaA == null && !a.contains("-")) {
			metaA = pathToMeta.get(getPathTable(a));
		}
		if(metaA == null) {
			OOAssert.fatal("a=%s", a);
		}
		//TODO 关系不对
		Map<String,String> mappingA = relMap.get(metaA.getName());
		if(mappingA == null) {
			mappingA = new HashMap<>();
			relMap.put(metaA.getName(), mappingA);
		}
		mappingA.put(meta.getName(), path);
		
		Map<String,String> mappingB = relMap.get(meta.getName());
		if(mappingB == null) {
			mappingB = new HashMap<>();
			relMap.put(meta.getName(), mappingB);
		}
		mappingB.put(metaA.getName(), path);
	}
	public Set<String> generateDenpendTableByParams(String mainAlias,Set<String> params) {
		Set<String> set = new HashSet<>();		
		if(params == null || params.isEmpty()) {
			return set;
		}
		for(String column:params) {
			DDMeta meta = columnToMeta.get(column);
			if(meta != null) {
				set.add(meta.getName());
			}else {
				OOAssert.warnning("列 %s 不存在", column);	
			}
		}
		Set<String> result = new HashSet<>();
		for(String alias:set) {
			if(result.contains(alias)) { // 在某个关系链中
				continue;
			}
			Set<String> spoor = new HashSet<>();
			List<String> path = new ArrayList<>();
			if(search(spoor,path,alias,mainAlias)) {
				result.addAll(path);
			}else {
				OOAssert.warnning("关系一定有错误，无法推导 %s -> %s", alias,mainAlias);	
			}
		}
		return result;
	}
	
	public boolean search(Set<String> spoor,List<String> path,String src,String dest) {
		if(spoor.contains(src)) {
			return false;
		}
		Map<String,String> map = relMap.get(src);
		if(map == null) {
			return false;
		}
		path.add(src);
		spoor.add(src);
		if(map.containsKey(dest)) {
			return true;
		}else  {
			for(String subSrc:map.keySet()) {
				if(search(spoor,path,subSrc,dest)) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	/**
	 * 主表和关联参数，生成一个SQL的join部分
	 * @param mainEntity
	 */
	public void generateJoinSql(String mainEntity,Set<String> params) {
		DDMeta mainMeta = pathToMeta.get(mainEntity);
		if(mainMeta == null) {
			OOAssert.warnning("% 不存在", mainEntity);
		}
		String mainAlias = mainMeta.getName();
		Set<String> classes = generateDenpendTableByParams(mainAlias, params);
		
		for(String clazz:classes) {
			System.out.println(aliasToMeta.get(clazz).getPath());
		}
		
		Set<String> columns = new HashSet<>();
		Set<String> missColumns = new HashSet<>();
		for(DDMeta meta:aliasToMeta.values()) {
			if(classes.contains(meta.getName())) {
				columns.addAll(meta.getColumns());
			}else {
				missColumns.addAll(meta.getColumns());
			}
		}
		
	}
	
	class DDMeta {
		String path;
		String name;
		String table;
		Map<String,String> columnMapping = new HashMap<>();
		
		public String getPath() {
			return path;
		}
		public void setPath(String path) {
			this.path = path;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getTable() {
			return table;
		}
		public void setTable(String table) {
			this.table = table;
		}
		public Set<String> getColumns() {
			return columnMapping.keySet();
		}
		public void setColumnMapping(Collection<String> columnAlias) {
			for(String column:columnAlias) {
				String[] rel_col = column.trim().split(" +");
				if(rel_col.length == 2) {
					columnMapping.put(rel_col[1], rel_col[0]);
				}else if(rel_col.length == 1){
					columnMapping.put(rel_col[0], rel_col[0]);
				}else {
					OOAssert.warnning("%s 不符合规范", column);
				}
			}
		}
		public Map<String, String> getColumnMapping() {
			return columnMapping;
		}
		public void setColumnMapping(Map<String, String> columnMapping) {
			this.columnMapping = columnMapping;
		}
		@Override
		public String toString() {
			return OOUtils.toJSON(this);
		}
		
	}
}
