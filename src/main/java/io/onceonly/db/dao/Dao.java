package io.onceonly.db.dao;

import java.util.List;
import java.util.function.Consumer;

import io.onceonly.db.dao.tpl.UpdateTpl;

public interface Dao<T,ID> {
	T get(ID id);
	T insert(T entity);
	int insert(List<T> entities);
	int update(T entity);
	int updateIgnoreNull(T entity);
	int updateByTmpl(T entity, UpdateTpl<T> tmpl);
	int updateByTmplCnd(T entity, UpdateTpl<T> tmpl,Cnd<T> cnd);
	int remove(ID id);
	int remove(List<ID> ids);
	int remove(Cnd<T> cnd);
	int delete(ID id);
	int delete(List<ID> ids);
	int delete(Cnd<T> cnd);
	
	List<T> findByIds(List<ID> ids);
	Page<T> findByEntity(T entity,Integer page,Integer pageSize);
	
	/**
	 * 数值为判等
	 * 字符串为正则匹配
	 */
	Page<T> find(Cnd<T> cnd);
	void download(Cnd<T> cnd,Consumer<T> consumer);
	
	long count();
	long count(Cnd<T> cnd);
}
