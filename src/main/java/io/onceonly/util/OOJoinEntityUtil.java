package io.onceonly.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.onceonly.db.BaseEntity;

/**
 * 内存间表关联
 **/
public class OOJoinEntityUtil {
	
	private Class<?> mainEntity;
	/** 每种类型，<主键，数据 >*/
	private Map<Class<?>,Map<Object,Object>> values = new HashMap<>();
	
	/** 推倒图 */
	private Map<Class<?>,Map<Class<?>,Set<String>>> deduceMap = new HashMap<>();

	public OOJoinEntityUtil(Class<?> mainEntity) {
		this.mainEntity = mainEntity;
	}
	public <T extends BaseEntity> OOJoinEntityUtil  putData(Class<T> type,List<T> data) {
		Map<Object, Object> valueMap = values.get(type);
		if (valueMap == null) {
			valueMap = new HashMap<>();
			values.put(type, valueMap);
		}
		for (BaseEntity be : data) {
			valueMap.put(be.getId(), be);
		}
		return this;
	}
	
	/**
	 * 通过 a.fieldName=b.id
	 * @param a
	 * @param fieldName
	 * @param b
	 * @return
	 */
	public OOJoinEntityUtil append(Class<?> a,String fieldName,Class<?> b){
		Map<Class<?>,Set<String>> relA = deduceMap.get(a);
		if(relA == null) {
			relA = new HashMap<>();
			deduceMap.put(a, relA);
		}
		Set<String> lastA = relA.get(b);
		if(lastA == null) {
			lastA = new HashSet<String>();
			relA.put(b, lastA);
		}
		lastA.add(fieldName);
		return this;
	}

	/** 主表ID,关系链中的所有类和对象 */
	public Map<Object,Map<Class<?>,Set<Object>>> build() {
		 Map<Object,Map<Class<?>,Set<Object>>> mapping = new HashMap<>();
		 Map<Object,Object> mainValues= values.get(mainEntity);
		 for(Object id:mainValues.keySet()) {
			 Object obj = mainValues.get(id);
			 Map<Class<?>,Set<Object>> result = new HashMap<>();
			 deduce(result,id,obj);
			 mapping.put(id, result);
		 }
		 
		 return mapping;
	}
	
	
	private void deduce(Map<Class<?>,Set<Object>> result,Object id,Object obj) {
		if(obj == null || id == null){
			return;
		}
		Set<Object> vals = result.get(obj.getClass());
		if(vals == null) {
			vals = new HashSet<>();
			result.put(obj.getClass(), vals);
		}
		/** 已经推倒过了 跳过 */
		if(vals.contains(obj)) {
			return;
		}
		vals.add(obj);
		Map<Class<?>,Set<String>> chain = deduceMap.get(obj.getClass());
		for(Class<?> genEntity:chain.keySet()) {
			Set<String> cnds = chain.get(genEntity);
			for(String fieldName:cnds){
				Object genId = getValueByFieldName(obj,fieldName);
				if(genId != null) {
					Object genVal = values.get(genEntity).get(genId);
					deduce(result,genId,genVal);
				}
			}
		}
		
	}
	
	private Object getValueByFieldName(Object obj,String fieldName) {
		try {
			return obj.getClass().getField(fieldName).get(obj);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
