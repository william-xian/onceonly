package io.onceonly.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.onceonly.util.OOUtils;
import io.onceonly.util.Tuple3;

public class SqlTask {
	private String joinSql;
	private List<Tuple3<Class<?>,String,Class<?>>> task;
	
	/** 实体类-提供的->字段 用户回填最终结果集 */
	private Map<Class<?>,Set<String>> missColumns = new HashMap<>();
	
	public String getJoinSql() {
		return joinSql;
	}
	public void setJoinSql(String joinSql) {
		this.joinSql = joinSql;
	}
	public List<Tuple3<Class<?>, String, Class<?>>> getTask() {
		return task;
	}
	public void setTask(List<Tuple3<Class<?>, String, Class<?>>> task) {
		this.task = task;
	}
	
	public Map<Class<?>, Set<String>> getMissColumns() {
		return missColumns;
	}
	public void setMissColumns(Map<Class<?>, Set<String>> missColumns) {
		this.missColumns = missColumns;
	}
	@Override
	public String toString() {
		HashMap<String,Object> obj = new HashMap<>();
		obj.put("joinSql", joinSql);
		if(task != null && missColumns != null) {
			List<String> list = new ArrayList<>();
			for(Tuple3<Class<?>,String,Class<?>> t:task) {
				list.add(String.format("{%s--%s-->%s}", t.a.getName(),t.b,t.c.getName()));
			}
			obj.put("task", list);
			Map<String,Set<String>> mcs = new HashMap<String,Set<String>>();
			for(Class<?> entity:missColumns.keySet()){
				mcs.put(entity.getName(), missColumns.get(entity));
			}
			obj.put("missColumns", mcs);
		}
		return OOUtils.toJSON(obj);
	}
	
}
