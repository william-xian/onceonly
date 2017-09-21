package io.onceonly.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于分析推倒关系，和依赖关系
 **/
public class Causality {

	private Map<Object,List<CauseEffect>> deduction = new HashMap<>();
	
	private Map<Object,List<CauseEffect>> depend = new HashMap<>();
	
	/**
	 * a[] --cnd--> b[];
	 */
	public Causality deduce(List<Object> cause,List<Object> effect,List<Object> cnd) {
		
		return this;
	}
	/**
	 * a --cnd--> b;
	 */
	public Causality deduce(Object cause,Object effect,List<Object> cnd) {
		
		return this;
	}
}

class CauseEffect {
	private Object cause;
	private Object effect;
	private List<Object> cnd;
}
