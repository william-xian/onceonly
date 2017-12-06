package io.onceonly.db.dao.impl;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.jdbc.core.JdbcTemplate;

import io.onceonly.db.dao.Cnd;
import io.onceonly.db.dao.Dao;
import io.onceonly.db.dao.Page;
import io.onceonly.db.dao.tpl.SelectTpl;
import io.onceonly.db.dao.tpl.UpdateTpl;


public class DaoImpl<E,ID> implements Dao<E,ID> {
	
	private JdbcTemplate jdbcTemplate;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public E get(ID id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public E insert(E entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int insert(List<E> entities) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(E entity) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateIgnoreNull(E entity) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByTpl(UpdateTpl<E> tmpl) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByTplCnd(UpdateTpl<E> tmpl, Cnd<E> cnd) {
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
	public int remove(Cnd<E> cnd) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int recovery(Cnd<E> cnd) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int delete(ID id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int delete(List<ID> ids) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int delete(Cnd<E> cnd) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<E> findByIds(List<ID> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<E> find(Cnd<E> cnd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<E> findTpl(SelectTpl<E> tpl, Cnd<E> cnd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void download(SelectTpl<E> tpl, Cnd<E> cnd, Consumer<E> consumer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long count(Cnd<E> cnd) {
		// TODO Auto-generated method stub
		return 0;
	}

}
