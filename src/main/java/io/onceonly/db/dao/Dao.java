package io.onceonly.db.dao;

import java.util.List;
import java.util.function.Consumer;

import io.onceonly.db.dao.tpl.SelectTpl;
import io.onceonly.db.dao.tpl.UpdateTpl;

public interface Dao<T,ID> {
	T get(ID id);
	T insert(T entity);
	int insert(List<T> entities);
	int update(T entity);
	int updateIgnoreNull(T entity);
	int updateByTpl(UpdateTpl<T,ID> tmpl);
	int updateByTplCnd(UpdateTpl<T,ID> tmpl,Cnd<T> cnd);
	int remove(ID id);
	int remove(List<ID> ids);
	int remove(Cnd<T> cnd);
	int recovery(Cnd<T> cnd);
	int delete(ID id);
	int delete(List<ID> ids);
	int delete(Cnd<T> cnd);
	List<T> findByIds(List<ID> ids);
	Page<T> find(Cnd<T> cnd);
	Page<T> findTpl(SelectTpl<T> tpl,Cnd<T> cnd);
	void download(SelectTpl<T> tpl,Cnd<T> cnd,Consumer<T> consumer);
	long count();
	long count(Cnd<T> cnd);
}
