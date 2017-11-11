package io.onceonly.db.dao.tpl;

import java.util.ArrayList;
import java.util.List;

/**
 * 遇到需要使用distinct时，请使用group by性能更好 
 */
public class SelectTpl<E> extends FuncTpl<E>{
	public SelectTpl(Class<E> tplClass) {
		super(tplClass);
	}
	public E using() {
		funcs.add("");
		return tpl;
	}
	public E usingOrderNum() {
		funcs.add("ROW_NUMBER() OVER()");
		return tpl;
	}
	public List<String> columns() {
		List<String> cols = new ArrayList<>();
		for(int i = 0; i < funcs.size(); i++) {
			String func = funcs.get(i);
			String argName = argNames.get(i);
			if(func.equals("")) {
				cols.add(argName);
			}else if(func.equals("ROW_NUMBER() OVER()")){
				cols.add("ORDERNUM_orderNum");
			}else {
				cols.add(String.format("%s_%s",func,argName));
			}
		}
		return cols;
	}
	public String sql() {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < funcs.size(); i++) {
			String func = funcs.get(i);
			String argName = argNames.get(i);
			if(func.equals("")) {
				sb.append(argName + ",");
			}else if(func.equals("ROW_NUMBER() OVER()")){
				sb.append(String.format("%s ORDERNUM_orderNum,",func));
			}else {
				sb.append(String.format("%s(%s) %s_%s,",func,argName,func,argName));
			}
		}
		if(sb.length() > 0) {
			sb.delete(sb.length()-1, sb.length());	
		}
		return sb.toString();
	}
}
