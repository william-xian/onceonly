package io.onceonly.db.dao.tpl;

import java.util.ArrayList;
import java.util.List;

import io.onceonly.util.OOLog;



/**  
 * @author xian
 * @param <E>
 * 
 * 
 * 
 */
public class HavingTpl<E> extends FuncTpl<E>{
	private List<Object> args = new ArrayList<>();
	private List<String> opts = new ArrayList<>();
	private List<String> logics = new ArrayList<>();
	private List<String> extLogics = new ArrayList<>();
	private List<HavingTpl<E>> extTpls = new ArrayList<>();

	public HavingTpl(Class<E> tplClass) {
		super(tplClass);
	}
	
	public E eq(Object arg) {
		opts.add("=");
		args.add(arg);
		return tpl;
	}
	public E ne(Object arg) {
		opts.add("!=");
		args.add(arg);
		return tpl;
	}
	public E lt(Object arg) {
		opts.add("<");
		args.add(arg);
		return tpl;
	}
	public E le(Object arg) {
		opts.add("<=");
		args.add(arg);
		return tpl;
	}
	public E gt(Object arg) {
		opts.add(">");
		args.add(arg);
		return tpl;
	}
	public E ge(Object arg) {
		opts.add(">=");
		args.add(arg);
		return tpl;
	}

	public HavingTpl<E> and() {
		logics.add("AND");
		return this;
	}
	
	public HavingTpl<E> or() {
		logics.add("OR");
		return this;
	}
	public HavingTpl<E> not() {
		logics.add("NOT");
		return this;
	}
	
	public HavingTpl<E> and(HavingTpl<E> tpl) {
		extLogics.add("AND");
		extTpls.add(tpl);
		return this;
	}
	
	public HavingTpl<E> or(HavingTpl<E> tpl) {
		extLogics.add("OR");
		extTpls.add(tpl);
		return this;
	}
	public HavingTpl<E> not(HavingTpl<E> tpl) {
		extLogics.add("NOT");
		extTpls.add(tpl);
		return this;
	}
	
	public String sql() {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < funcs.size(); i++) {
			String func = funcs.get(i);
			String argName = argNames.get(i);
			String logic = "";
			if(i < logics.size()) {
				logic = logics.get(i);
			}
			String opt = opts.get(i);
			if(opt != null) {
				sb.append(String.format("%s(%s) %s ? %s"	, func,argName,opt,logic));
			}
		}
		for(int i = 0; i < extTpls.size(); i++) {
			String extLogic = extLogics.get(i);
			String extSql = extTpls.get(i).sql();
			if(!extSql.equals("")) {
				sb.append(String.format("%s (%s)", extLogic,extTpls.get(i).sql()));	
			}else {
				OOLog.warnning("the sql of having's %s is empty", extLogic);
			}
		}
		return sb.toString();
	}	
}
