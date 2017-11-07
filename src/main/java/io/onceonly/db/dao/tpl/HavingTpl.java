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
	private List<SqlOpt> opts = new ArrayList<>();
	private List<SqlLogic> logics = new ArrayList<>();
	private List<SqlLogic> extLogics = new ArrayList<>();
	private List<HavingTpl<E>> extTpls = new ArrayList<>();

	public HavingTpl(Class<E> tplClass) {
		super(tplClass);
	}
	
	public E eq(Object arg) {
		opts.add(SqlOpt.EQ);
		args.add(arg);
		return tpl;
	}
	public E ne(Object arg) {
		opts.add(SqlOpt.NE);
		args.add(arg);
		return tpl;
	}
	public E lt(Object arg) {
		opts.add(SqlOpt.LT);
		args.add(arg);
		return tpl;
	}
	public E le(Object arg) {
		opts.add(SqlOpt.LE);
		args.add(arg);
		return tpl;
	}
	public E gt(Object arg) {
		opts.add(SqlOpt.GT);
		args.add(arg);
		return tpl;
	}
	public E ge(Object arg) {
		opts.add(SqlOpt.GE);
		args.add(arg);
		return tpl;
	}

	public HavingTpl<E> and() {
		logics.add(SqlLogic.AND);
		return this;
	}
	
	public HavingTpl<E> or() {
		logics.add(SqlLogic.OR);
		return this;
	}
	public HavingTpl<E> not() {
		logics.add(SqlLogic.NOT);
		return this;
	}
	
	public HavingTpl<E> and(HavingTpl<E> tpl) {
		extLogics.add(SqlLogic.AND);
		extTpls.add(tpl);
		return this;
	}
	
	public HavingTpl<E> or(HavingTpl<E> tpl) {
		extLogics.add(SqlLogic.OR);
		extTpls.add(tpl);
		return this;
	}
	public HavingTpl<E> not(HavingTpl<E> tpl) {
		extLogics.add(SqlLogic.NOT);
		extTpls.add(tpl);
		return this;
	}
	
	public String sql() {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < funcs.size(); i++) {
			String func = funcs.get(i);
			String argName = argNames.get(i);
			SqlOpt opt = opts.get(i);
			String logic = "";
			if(i < logics.size()) {
				logic = logics.get(i).name();
			}
			String strOpt = null;
			switch(opt) {
			case EQ:
				strOpt = "=";
				break;
			case GT:
				strOpt = ">";
				break;
			case GE:
				strOpt = ">=";
				break;
			case LT:
				strOpt = "<";
				break;
			case LE:
				strOpt = "<=";
				break;
			case LIKE:
				strOpt = "like";
				break;
			case PATTERN:
				strOpt = "~*";
				break;
			case IN:
				strOpt = "in";
				break;
				default:
			}
			if(strOpt != null) {
				sb.append(String.format("%s(%s) %s ? %s"	, func,argName,strOpt,logic));
			}else {
				OOLog.warnning("%s", opt.name());
			}
		}
		for(int i = 0; i < extTpls.size(); i++) {
			SqlLogic extLogic = extLogics.get(i);
			String extSql = extTpls.get(i).sql();
			if(!extSql.equals("")) {
				sb.append(String.format("%s (%s)", extLogic.name(),extTpls.get(i).sql()));	
			}else {
				OOLog.warnning("the sql of having's %s is empty", extLogic.name());
			}
		}
		return sb.toString();
	}	
}
