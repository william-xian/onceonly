package io.onceonly.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.onceonly.db.meta.DDMeta;
import io.onceonly.db.meta.TableMeta;
import io.onceonly.util.OOAssert;
import io.onceonly.util.OOUtils;

public class DDHoster {
	public static final Map<String,Class<?>> tableToEntity = new HashMap<>();
	public static final List<TableMeta> oldTableMeta = new ArrayList<>();
	public static final Map<String,TableMeta> tableToTableMeta= new HashMap<>();
	public static final Map<String,DDMeta> tableToDDMeta = new HashMap<>();
	public static final List<TableMeta> missTableMeta = new ArrayList<>(0);
	public static final Map<String,List<String>> family = new HashMap<>();
	public static final Map<String,String> sonToFather = new HashMap<>();
	
	public static void putEntity(Class<?> entity) {
		String table = entity.getName();
		if(!tableToEntity.containsKey(table)) {
			TableMeta tm = TableMeta.createBy(entity);
			if(tm != null) {
				tableToEntity.put(table, entity);
				tableToTableMeta.put(table, tm);		
			}			
		}
	}
	public static void putEntities(List<Class<?>> entities) {
		for(Class<?> entity:entities){
			putEntity(entity);
		}
	}
	
	public static List<String> upgrade() {
		List<String> sqlTask = new ArrayList<>();
		Set<String> needToAdd = new HashSet<>(tableToEntity.keySet());
		missTableMeta.clear();
		for(TableMeta oldtm:oldTableMeta){
			TableMeta newtm = tableToTableMeta.get(oldtm.getTable());
			if(newtm != null && !newtm.equals(oldtm)) {
				tableToTableMeta.put(newtm.getTable(), newtm);
				sqlTask.add(oldtm.upgradeTo(newtm));
				needToAdd.remove(newtm.getTable());
			}else if(newtm == null) {
				missTableMeta.add(oldtm);
			}
		}
		for(String newtable:needToAdd) {
			TableMeta newtm = tableToTableMeta.get(newtable);
			sqlTask.add(newtm.createTableSql());
		}
		
		updateRelation();
		
		return sqlTask;
	}
	private static void updateRelation() {
		family.clear();
		sonToFather.clear();
		for(TableMeta tm:tableToTableMeta.values()) {
			List<String> fatherSonChain =  new ArrayList<>();
			Set<String> spoor = new HashSet<>();
			fatherSonChain.add(tm.getTable());
			spoor.add(tm.getTable());
			String father = tm.getExtend();
			while(father != null) {
				if(spoor.contains(father)) {
					OOAssert.fatal("存在环形扩展关系 %s", OOUtils.toJSON(spoor));
				}
				fatherSonChain.add(0, father);
				
				TableMeta fatherMeta = tableToTableMeta.get(father);
				if(fatherMeta == null) {
					OOAssert.fatal("被扩展的table（%s）不存在", father);
				}
				father = fatherMeta.getExtend();
			}
			family.put(fatherSonChain.get(0), fatherSonChain);
			sonToFather.put(fatherSonChain.get(fatherSonChain.size()-1), fatherSonChain.get(0));
		}
	}
	
}
