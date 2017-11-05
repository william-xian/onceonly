package io.onceonly.db.dao.tpl;

import java.util.ArrayList;
import java.util.List;

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
		return null;
	}	
}
