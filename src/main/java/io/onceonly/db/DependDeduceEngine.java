package io.onceonly.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.onceonly.util.OOAssert;

public class DependDeduceEngine {
	
	/**
	 * 表以及对应的字段
	 */
	Map<String,String> tableColumn = new HashMap<>();
	
	/**
	 * 虚表字段，表来源
	 */
	Map<String,String> columnTable =  new HashMap<>();
	
	private	Map<String,Map<String,Set<String>>> relMap= new HashMap<>();
	
	private void analyse(String relation) {
		String rels[] = relation.split(",");
		for(String rel:rels) {
			String[] tbls = rel.split("=");
			if(tbls.length == 2) {
				String a = tbls[0].trim();
				String b = tbls[1].trim();
				if(!a.equals("") && !b.equals("")) {
					String[] a_key = a.split(".");
					String[] b_key = b.split(".");
					if(a_key.length == 2 && b_key.length == 2) {
						Map<String,Set<String>> a2b = relMap.get(a_key[0]);
						if(a2b == null) {
							a2b = new HashMap<>();
							relMap.put(a_key[0], a2b);
						}
						Set<String> abMapping = a2b.get(b_key[0]);
						if(abMapping == null) {
							abMapping = new HashSet<>();
							a2b.put(b_key[0], abMapping);
						}
						abMapping.add(relation);
						Map<String,Set<String>> b2a = relMap.get(b_key[0]);
						if(b2a == null) {
							b2a = new HashMap<>();
							relMap.put(b_key[0], b2a);
						}
						Set<String> baMapping = b2a.get(a_key[0]);
						if(baMapping == null) {
							baMapping = new HashSet<>();
							b2a.put(a_key[0], baMapping);
						}
						baMapping.add(relation);
						
					}else {
						OOAssert.warnning("关系必须是<表名>.<主键>");
					}
				}
			}else {
				OOAssert.warnning("关系必须是<表名>.<主键> = <表名>.<主键>的格式");

			}
		}
	}
	
	private void resolve(String result) {
		String[] sets = result.split(";");
		for(String set:sets) {
			String[] tbl_columns = set.split("\\{|\\}");
			if(tbl_columns.length >=2) {
				String tbl = tbl_columns[0];
				String[] columns = tbl_columns[1].split(",");
				for(String column:columns) {
					String[] rel_col = column.trim().split(" +");
					String col = rel_col.length == 2?rel_col[1].trim():rel_col[0].trim();
					tableColumn.put(tbl.trim() + "." + rel_col[0].trim(),col);
					if(!columnTable.containsKey(col)) {
						columnTable.put(col, tbl.trim() + "." + rel_col[0].trim());		
					} else {
						String before = columnTable.get(col);
						if(!before.equals(tbl.trim() + "." + rel_col[0].trim())) {
							OOAssert.warnning("column:%来自不同的两个表", col);	
						}
					}
				}
			}else {
				OOAssert.warnning("%s -> %s 不合法", result, set);
			}
		}
		
	}

	/**
	 * 添加推导关系和结果集 <b>此处的关系是一一对应的</b>
	 * @param relation A.bid=B.id,B.cid=C.id
	 * @param result   A{id aid,name AName,bid}; B{name BName,CId},C{name CName}
	 * @return
	 */
	public DependDeduceEngine append(String relation,String result) {
		analyse(relation);
		resolve(result);
		return this;
	}
	
	private Set<String> generateDenpendTableByParams(String mainEntity,Set<String> params) {
		Set<String> set = new HashSet<>();		
		if(params == null || params.isEmpty()) {
			return set;
		}
		for(String column:params) {
			String orignal = columnTable.get(column);
			set.add(orignal.split(".")[0]);
			
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
		Map<String,Set<String>> map = relMap.get(src);
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
		Set<String> classes = generateDenpendTableByParams(mainEntity, params);
		Set<String> columns = new HashSet<>();
		Set<String> missColumns = new HashSet<>();
		for(String clazzColumn:tableColumn.keySet()) {
			String clazz = clazzColumn.split(".")[0];
			if(classes.contains(clazz)) {
				columns.add(tableColumn.get(clazzColumn));
			}else {
				missColumns.add(tableColumn.get(clazzColumn));
			}
		}
		
	}
}
