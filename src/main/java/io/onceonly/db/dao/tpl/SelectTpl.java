package io.onceonly.db.dao.tpl;

public class SelectTpl<E> extends FuncTpl<E>{
	public SelectTpl(Class<E> tplClass) {
		super(tplClass);
	}
	public E using() {
		funcs.add("");
		return tpl;
	}
	public E distinct() {
		funcs.add("DISTINCT");
		return tpl;
	}
	public String sql() {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < funcs.size(); i++) {
			String func = funcs.get(i);
			String argName = argNames.get(i);
			if(func.equals("")) {
				sb.append(argName + ",");
			}else {
				sb.append(String.format("%s(%s) %s,",func,argName,argName));
			}
		}
		if(sb.length() > 0) {
			sb.delete(sb.length()-1, sb.length());	
		}
		return sb.toString();
	}
}
