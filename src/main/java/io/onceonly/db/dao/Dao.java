package io.onceonly.db.dao;

import java.util.List;
import java.util.function.Consumer;

public interface Dao<T,ID> {
	T get(ID id);
	T insert(T entity);
	int insert(List<T> entities);
	int update(T entity);
	int updateIgnoreNull(T entity);
	int updateIncrement(T increment);
	int remove(ID id);
	int remove(List<ID> ids);
	int delete(ID id);

	List<T> findByIds(List<ID> ids);
	
	/**
	 * 数值为判等
	 * 字符串为正则匹配
	 */
	Page<T> findByEntity(T entity,Integer page,Integer pageSize);
	
	int update(T newVal,String pattern,Cnd<T> cnd);
	/**
	 * 对于字符串，将会被拼接起来
	 * 对于数值进行累计
	 * boolean做与运算
	 * @param increment
	 * @param cnd
	 * @return
	 */
	int updateIncrement(T increment,Cnd<T> cnd);
	/** 数值型数据做异或运算 */
	int updateXOR(T arg,Cnd<T> cnd);
	int remove(Cnd<T> cnd);
	int delete(Cnd<T> cnd);
	Page<T> search(Cnd<T> cnd);
	List<T> find(Cnd<T> cnd);
	void download(Cnd<T> cnd,Consumer<T> consumer);
	
	long count();
	long count(Cnd<T> cnd);
}
