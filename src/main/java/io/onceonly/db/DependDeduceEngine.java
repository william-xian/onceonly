package io.onceonly.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.onceonly.util.OOAssert;
import io.onceonly.util.OOUtils;

public class DependDeduceEngine {
	
	/**
	 * 表以及对应的字段
	 */
	public Map<String,String> aliasToColumn = new HashMap<>();
	public Map<String,String> aliasToTable= new HashMap<>();
	public Map<String,String> tableToEntity = new HashMap<>();
	/**
	 * 虚表字段，表来源
	 */
	public Map<String,String> columnTableAlias =  new HashMap<>();
	
	/**
	 * <表，<关联表，关联关系>>
	 */
	public	Map<String,Map<String,String>> relMap= new HashMap<>();
	
	/**
	 * 添加推导关系和结果集 <b>此处的关系是一一对应的</b>
	 * @param result   A {id aid,name AName,bid}; A.bid>B {name BName,CId}; A.bid-B.cid>C {name CName}
	 * @return
	 */
	public void resolve(String resultSet) {
		String[] sets = resultSet.split(";");
		for(String set:sets) {
			String[] tbl_columns = set.split("\\{|\\}");
			if(tbl_columns.length >=2) {
				String path = tbl_columns[0].trim();
				String pt[] = path.split(">");
				String tbl = pt[pt.length-1];
				String alias = path.replace('.', '_').replace('-', '_').replace('>', '_');
				String[] columns = tbl_columns[1].split(",");
				aliasToTable.put(alias, tbl);
				for(String column:columns) {
					String[] rel_col = column.trim().split(" +");
					String col = rel_col.length == 2?rel_col[1].trim():rel_col[0].trim();
					aliasToColumn.put(alias + "." + rel_col[0].trim(),col);
					if(!columnTableAlias.containsKey(col)) {
						columnTableAlias.put(col, alias + "." + rel_col[0].trim());		
					} else {
						String before = columnTableAlias.get(col);
						if(!before.equals(alias + "." + rel_col[0].trim())) {
							OOAssert.warnning("column:%来自不同的两个表", col);	
						}
					}
				}
			}else {
				OOAssert.warnning("%s -> %s 不合法", resultSet, set);
			}
		}
	}
	
	public Set<String> generateDenpendTableByParams(String mainEntity,Set<String> params) {
		Set<String> set = new HashSet<>();		
		if(params == null || params.isEmpty()) {
			return set;
		}
		for(String column:params) {
			String orignal = columnTableAlias.get(column);
			if(orignal != null) {
				String[] oarr = orignal.split("\\.");
				System.out.println(OOUtils.toJSON(oarr));
				set.add(oarr[0]);
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
			if(search(spoor,path,alias,mainEntity)) {
				result.addAll(path);
			}else {
				OOAssert.warnning("关系一定有错误，无法推导 %s -> %s", alias,mainEntity);	
			}
		}
		return result;
	}
	
	public boolean search(Set<String> spoor,List<String> path,String src,String dest) {
		if(spoor.contains(src)) {
			return false;
		}
		Map<String,String> map = relMap.get(src);
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
	
	
	/** TODO
	 * 主表和关联参数，生成一个SQL的join部分
	 * @param mainEntity
	 */
	public void generateJoinSql(String mainEntity,Set<String> params) {
		Set<String> classes = generateDenpendTableByParams(mainEntity, params);
		Set<String> columns = new HashSet<>();
		Set<String> missColumns = new HashSet<>();
		for(String clazzColumn:aliasToColumn.keySet()) {
			String clazz = clazzColumn.split("\\.")[0];
			if(classes.contains(clazz)) {
				columns.add(aliasToColumn.get(clazzColumn));
			}else {
				missColumns.add(aliasToColumn.get(clazzColumn));
			}
		}
		
	}
}
