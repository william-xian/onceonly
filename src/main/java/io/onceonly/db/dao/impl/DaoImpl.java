package io.onceonly.db.dao.impl;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import io.onceonly.db.dao.Cnd;
import io.onceonly.db.dao.Dao;
import io.onceonly.db.dao.Page;

@Component
public class DaoImpl<T,ID> implements Dao<T,ID> {
	@Autowired
	private JdbcTemplate template;

	public JdbcTemplate getTemplate() {
		return template;
	}

	public void setTemplate(JdbcTemplate template) {
		this.template = template;
	}

	@Override
	public T get(ID id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T insert(T entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int insert(List<T> entities) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(T entity) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(T entity, String pattern) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateIgnoreNull(T entity) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateIgnore(T entity, String pattern) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateIncrement(T increment) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int remove(ID id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int remove(List<ID> ids) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int delete(ID id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(T newVal, String pattern, Cnd cnd) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateIncrement(T increment, Cnd cnd) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateXOR(T arg, Cnd cnd) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int remove(Cnd cnd) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int delete(Cnd cnd) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Page<T> search(Cnd cnd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void download(Cnd cnd, Consumer<T> consumer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long count(Cnd cnd) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<T> find(Cnd cnd) {
		// TODO Auto-generated method stub
		return null;
	}

}
