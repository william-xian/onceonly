package io.onceonly.db.dao;

import java.util.ArrayList;
import java.util.List;

public class Cnd<E> {
	private Integer page;
	private Integer pageSize;
	private String orderBy;
	private E e;
	private List<Object> vals = new ArrayList<>();
	public Cnd() {
	}
	public Cnd(E e) {
		this.e = e;
	}
	public Cnd<E> vals(Object[] vals) {
		if(e != null) {
			//TODO
			this.vals.add(vals);		
		}
		return this;
	}
	public Cnd<E> and(Cnd<E> cnd) {
		return this;
	}
	public Integer getPage() {
		return page;
	}

	public Cnd<E> setPage(Integer page) {
		this.page = page;
		return this;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public Cnd<E> setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public Cnd<E> eq(E e) {
		return this;
	}
	
}
