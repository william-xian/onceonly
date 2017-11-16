package io.onceonly.db.meta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.onceonly.util.OOAssert;
import io.onceonly.util.Tuple3;

/**
 * 数据推倒引擎
 * @author Administrator
 *
 */
public class DDEngine {
	
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
	public DDEngine append(String resultSet) {
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
				String tbl = curTbl.replaceAll("\\..*", "");
				if(tbl.equals(lastTbl)) {
					List<String> plist = new ArrayList<>();
					for(int t = 0; t < i; t++) {
						plist.add(pathArr[t]);
					}
					plist.add(tbl);
					String pkName = pathArr[i-1].replaceAll(".*\\.", "");
					String npath = String.join("-", plist);
					if(!pathToMeta.containsKey(npath)) {
						DDMeta  nmeta = new DDMeta();
						nmeta.setPath(npath);
						nmeta.setPkName(pkName);
						pathToMeta.put(npath, nmeta);	
					}
				}
				lastTbl = tbl;
			}
		}
	}

	
	private Tuple3<String,String,String> splitPath(String path) {
		Tuple3<String,String,String> result = new Tuple3<String,String,String>();
		int sep = path.lastIndexOf('-');
		if(sep < 0) {
			result.a = path;
		}else {
			String rel = path.substring(0, sep);
			int rSep = rel.lastIndexOf('.');
			result.a = path.substring(0, rSep);
			result.b = path.substring(sep+1);
			result.c = path.substring(rSep+1,sep);
		}
		return result;
	}
	
	private void resoveDDMeta(DDMeta meta) {
		Tuple3<String,String,String> tbls = splitPath(meta.getPath());
		if(tbls.a != null && tbls.b == null) {
			meta.setTable(tbls.a);
			return;
		}else {
			meta.setTable(tbls.b.replaceAll("\\..*", ""));	
		}
		DDMeta metaA = pathToMeta.get(tbls.a);
		if(meta.getPkName() != null) {
			/** 是中间表  */
			tbls = splitPath(tbls.a);
			metaA = pathToMeta.get(tbls.a);
		}
		if(metaA != null) {
			String rel = String.format("%s.%s = %s.%s", metaA.getName(),tbls.c,meta.getName(),meta.getPkName()==null?"id":meta.getPkName());
			saveRelation(rel,meta,metaA);
		}else {
			OOAssert.fatal("不可能,%s 对到错误", meta.getPath());
		}
	}
	private void saveRelation(String relation,DDMeta a,DDMeta b) {
		Map<String,String> mappingA = relMap.get(a.getName());
		if(mappingA == null) {
			mappingA = new HashMap<>();
			relMap.put(a.getName(), mappingA);
		}
		mappingA.put(b.getName(), relation);
		Map<String,String> mappingB = relMap.get(b.getName());
		if(mappingB == null) {
			mappingB = new HashMap<>();
			relMap.put(b.getName(), mappingB);
		}
		mappingB.put(a.getName(), relation);
	}

	/**
	 * 根据主表和相关参数 推导出依赖的相关表
	 * @param mainEntity
	 * @param params
	 * @return
	 */
	public SqlParamData deduceDependByParams(String mainPath,Set<String> params) {
		DDMeta mainMeta = pathToMeta.get(mainPath);
		SqlParamData spd = new SqlParamData();
		if(mainMeta == null) {
			OOAssert.warnning("%s 不存在", mainPath);
		}
		Set<DDMeta> set = new HashSet<>();		
		if(params != null && !params.isEmpty()) {
			for(String column:params) {
				DDMeta meta = columnToMeta.get(column);
				if(meta != null) {
					set.add(meta);
				}else {
					OOAssert.warnning("列 %s 不存在", column);	
				}
			}
		}
		Map<String,DDMeta> namePathToMeta = new HashMap<>();
		Set<DDMeta> depends = new HashSet<>();
		List<String> dependNamePaths = new ArrayList<>();
		for(DDMeta meta:set) {
			Set<DDMeta> spoor = new HashSet<>();
			List<DDMeta> path = new ArrayList<>();
			if(search(spoor,path,meta,mainMeta)) {
				depends.addAll(path);
				StringBuffer namepath = new StringBuffer();
				for(int i = path.size() -1 ; i >=0; i--) {
					namepath.append(path.get(i).getName()+"-");
				}
				namePathToMeta.put(namepath.toString(), meta);
				dependNamePaths.add(namepath.toString());
			}else {
				OOAssert.warnning("关系一定有错误，无法推导 %s -> %s", meta,mainPath);	
			}
		}
		Set<DDMeta> supplements = new HashSet<>();
		for(DDMeta meta:aliasToMeta.values()) {
			if(!depends.contains(meta)) {
				supplements.add(meta);
			}
		}
		List<String> supplementNamePaths = new ArrayList<>(supplements.size());
		for(DDMeta meta:supplements) {
			Set<DDMeta> spoor = new HashSet<>();
			List<DDMeta> path = new ArrayList<>();
			if(search(spoor,path,meta,mainMeta)) {
				StringBuffer namepath = new StringBuffer();
				for(int i = path.size() -1 ; i >=0; i--) {
					namepath.append(path.get(i).getName()+"-");
				}
				namepath.delete(namepath.length()-1, namepath.length());
				namePathToMeta.put(namepath.toString(), meta);
				supplementNamePaths.add(namepath.toString());
			}else {
				OOAssert.warnning("关系一定有错误，无法推导 %s -> %s", meta,mainPath);	
			}
		}
		spd.setMain(mainMeta);
		Collections.sort(dependNamePaths);
		Collections.sort(supplementNamePaths);
		spd.setDepends(depends);
		spd.setDependNamePaths(dependNamePaths);
		spd.setSupplements(supplements);
		spd.setSupplementNamePaths(supplementNamePaths);
		spd.setNamePathToMeta(namePathToMeta);;
		return spd;
	}
	/** 
	 * 寻找足迹
	 */
	public boolean search(Set<DDMeta> spoor,List<DDMeta> path,DDMeta src,DDMeta dest) {
		if(spoor.contains(src)) {
			return false;
		}
		Map<String,String> map = relMap.get(src.getName());
		if(map == null) {
			return false;
		}
		path.add(src);
		spoor.add(src);
		if(map.containsKey(dest.getName())) {
			path.add(dest);
			return true;
		}else  {
			for(String subSrc:map.keySet()) {
				DDMeta subMeta = aliasToMeta.get(subSrc);
				if(search(spoor,path,subMeta,dest)) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	public void generateSql(SqlParamData data) {
		DDMeta mainMeta = data.getMain();
		Set<DDMeta> depends = data.getDepends();
		List<String> dnps = data.getDependNamePaths();
		depends.add(mainMeta);
		StringBuffer sql = new StringBuffer("select ");
		for(DDMeta meta:depends) {
			Map<String,String> c2o = meta.getColumnToOrigin();
			for(String column:c2o.keySet()) {
				sql.append(String.format("%s.%s %s, ", meta.getName(),c2o.get(column),column));
			}
		}
		//删除最后两个字符：逗号空格
		sql.delete(sql.length()-2, sql.length());
		sql.append(String.format("\nfrom %s %s", mainMeta.getTable(), mainMeta.getName()));
		if(dnps != null && !dnps.isEmpty()) {
			Set<String> spoor = new HashSet<>();
			spoor.add(mainMeta.getName());
			for(String dnp:dnps) {
				String[] deps = dnp.split("-");
				String depend = deps[0];
				for(int i = 1; i < deps.length; i++) {
					DDMeta meta = aliasToMeta.get(deps[i]);
					/** 如果存在中间表去除重复 */
					if(spoor.contains(meta.getName())) continue;
					spoor.add(meta.getName());
					DDMeta dependMeta  = aliasToMeta.get(depend);
					String rel = relMap.get(meta.getName()).get(dependMeta.getName());
					sql.append(String.format("\nleft join %s %s on %s", meta.getTable(),meta.getName(),rel));
					depend = deps[i];
				}
				
			}
				
		}
		
		data.setSql(sql.toString());
	}
	
}
