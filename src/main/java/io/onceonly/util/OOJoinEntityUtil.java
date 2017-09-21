package io.onceonly.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.onceonly.db.BaseEntity;

/**
 * 内存间表关联
 **/
public class OOJoinEntityUtil {
	
	/** 每种类型，所有类以及对象 */
	private Map<Class<?>,Map<Object,Object>> values = new HashMap<>();
	/** 推倒链 */
	private Map<Class<?>,List<Tuple3<Class<?>,String,String>>> deduceChain = new HashMap<>();
	
	public OOJoinEntityUtil(Class<?> mainEntity) {
		
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
	
	public OOJoinEntityUtil append(Class<?> a,String ak,Class<?> b,String bk){
		return  this;
	}

	/** 主表ID,关系链中的所有类和对象 */
	Map<Object,Map<Class<?>,Object>> build() {
		 Map<Object,Map<Class<?>,Object>> mapping = new HashMap<>();
		 return mapping;
	}
	
}
