package io.onceonly.db.dao.tpl;

public class SelectTpl<E> extends FuncTpl<E>{
	public SelectTpl(Class<E> tplClass) {
		super(tplClass);
	}
	public E using() {
		return tpl;
	}
	public E distinct() {
		return tpl;
	}
}
