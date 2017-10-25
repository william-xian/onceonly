package io.onceonly.db.dao;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import io.onceonly.db.meta.DDMeta;
import io.onceonly.db.meta.TableMeta;
import io.onceonly.util.OOAssert;

public class DaoHelper {
	private JdbcTemplate jdbcTemplate;

	private Map<String,TableMeta> nameToTableMata;
	@SuppressWarnings("rawtypes")
	private Map<String,RowMapper> nameToRowMapper = new HashMap<>();
	private Map<String,DDMeta> nameToDDMata;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public Map<String, TableMeta> getNameToTableMata() {
		return nameToTableMata;
	}

	public void setNameToTableMata(Map<String, TableMeta> nameToTableMata) {
		this.nameToTableMata = nameToTableMata;
	}

	@SuppressWarnings("rawtypes")
	public Map<String, RowMapper> getNameToRowMapper() {
		return nameToRowMapper;
	}

	@SuppressWarnings("rawtypes")
	public void setNameToRowMapper(Map<String, RowMapper> nameToRowMapper) {
		this.nameToRowMapper = nameToRowMapper;
	}

	public Map<String, DDMeta> getNameToDDMata() {
		return nameToDDMata;
	}

	public void setNameToDDMata(Map<String, DDMeta> nameToDDMata) {
		this.nameToDDMata = nameToDDMata;
	}
	
	public <T> boolean createOrUpdate(Class<T> tbl) {
		TableMeta old = nameToTableMata.get(tbl.getSimpleName());
		if(old == null) {
			old = TableMeta.createBy(tbl);
			List<String> sqls = old.createTableSql();
			jdbcTemplate.batchUpdate(sqls.toArray(new String[0]));
			old.freshNameToField();
			nameToTableMata.put(tbl.getSimpleName(), old);
			return true;
		}else {
			TableMeta tm = TableMeta.createBy(tbl);
			if(old.equals(tm)){
				return false;
			} else if(tm != null){
				List<String> sqls = old.upgradeTo(tm);
				jdbcTemplate.batchUpdate(sqls.toArray(new String[0]));
				tm.freshNameToField();
				nameToTableMata.put(tbl.getSimpleName(), tm);
				return true;
			}
		}
		return false;
	}

	public <T> boolean drop(Class<T> tbl) {
		TableMeta tm = nameToTableMata.get(tbl.getSimpleName());
		if(tm == null) {
			return false;
		}
		String sql = String.format("DROP TABLE IF EXIST %s;", tbl.getSimpleName());
		jdbcTemplate.batchUpdate(sql);
		return true;
	}

	public int[] batchUpdate(final String... sql) throws DataAccessException {
		return jdbcTemplate.batchUpdate(sql);
	}
	
	public int[] batchUpdate(final String sql,List<Object[]> batchArgs) throws DataAccessException {
		return jdbcTemplate.batchUpdate(sql, batchArgs);
	}
	
	private <T> RowMapper<T> genRowMapper(Class<T> tbl,TableMeta tm) {
		RowMapper<T> rowMapper = new RowMapper<T>(){
			@Override
			public T mapRow(ResultSet rs, int rowNum) throws SQLException {
				T row = null;
				if(rs.next()) {
					try {
						row = tbl.newInstance();
						Map<String,Field> nameToField = tm.getNameToField();
						for(String fieldName:nameToField.keySet()) {
							Field field = nameToField.get(fieldName);
							Object val = rs.getObject(fieldName, field.getType());
							field.set(row, val);
						}
						
					} catch (InstantiationException | IllegalAccessException e) {
						OOAssert.warnning("%s InstantiationException", tbl);
					} 
				}
				return row;
			}
		};
		return rowMapper;
	}
	
	@SuppressWarnings("unchecked")
	public <T,ID> T get(Class<T> tbl,ID id) {
		TableMeta tm = nameToTableMata.get(tbl.getSimpleName());
		if(tm == null) {
			return null;
		}
		RowMapper<T> rowMapper = null;
		if(!nameToRowMapper.containsKey(tbl.getSimpleName())) {
			rowMapper = genRowMapper(tbl,tm);
			nameToRowMapper.put(tbl.getSimpleName(), rowMapper);
		}else {
			rowMapper = nameToRowMapper.get(tbl.getSimpleName());	
		}
		String sql = String.format("SELECT * FROM %s WHERE %s = (?)", tm.getTable(),tm.getPrimaryKey());
		List<T> values = jdbcTemplate.query(sql, new Object[]{id}, rowMapper);
		if(values.size() == 1){
			return values.get(0);
		}
		return null;
	}
	
	

	public <T> T insert(T entity) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public <T> int insert(List<T> entities) {
		// TODO Auto-generated method stub
		return 0;
	}

	public <T> int update(T entity) {
		// TODO Auto-generated method stub
		return 0;
	}

	public <T> int update(T entity, String pattern) {
		// TODO Auto-generated method stub
		return 0;
	}

	public <T> int updateIgnoreNull(T entity) {
		// TODO Auto-generated method stub
		return 0;
	}

	public <T> int updateIgnore(T entity, String pattern) {
		// TODO Auto-generated method stub
		return 0;
	}

	public <T> int updateIncrement(T increment) {
		// TODO Auto-generated method stub
		return 0;
	}

	public <T,ID> int remove(Class<T> tbl,ID id) {
		// TODO Auto-generated method stub
		return 0;
	}

	public <T,ID> int remove(Class<T> tbl, List<ID> ids) {
		// TODO Auto-generated method stub
		return 0;
	}

	public <T,ID> int delete(Class<T> tbl, ID id) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public <T> int update(T newVal, String pattern, Cnd cnd) {
		// TODO Auto-generated method stub
		return 0;
	}

	public <T> int updateIncrement(T increment, Cnd cnd) {
		// TODO Auto-generated method stub
		return 0;
	}

	public <T> int updateXOR(T arg, Cnd cnd) {
		// TODO Auto-generated method stub
		return 0;
	}

	public <T> int remove(Class<T> tbl, Cnd cnd) {
		// TODO Auto-generated method stub
		return 0;
	}

	public <T> int delete(Class<T> tbl,Cnd cnd) {
		// TODO Auto-generated method stub
		return 0;
	}

	public <T> Page<T> search(Class<T> tbl,Cnd cnd) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> void download(Cnd cnd, Consumer<T> consumer) {
		// TODO Auto-generated method stub
		
	}

	public <T> long count(Class<T> tbl) {
		// TODO Auto-generated method stub
		return 0;
	}

	public <T> long count(Class<T> tbl, Cnd cnd) {
		// TODO Auto-generated method stub
		return 0;
	}

	public <T> List<T> find(Class<T> tbl, Cnd cnd) {
		return null;
	}
	@SuppressWarnings("unchecked")
	public <T,ID> List<T> findByIds(Class<T> tbl, List<ID> ids) {
		if(ids.isEmpty()) {
			return new ArrayList<T>();
		}
		TableMeta tm = nameToTableMata.get(tbl.getSimpleName());
		if(tm == null) {
		}
		RowMapper<T> rowMapper = null;
		if(!nameToRowMapper.containsKey(tbl.getSimpleName())) {
			rowMapper = genRowMapper(tbl,tm);
			nameToRowMapper.put(tbl.getSimpleName(), rowMapper);
		}else {
			rowMapper = nameToRowMapper.get(tbl.getSimpleName());	
		}
		StringBuffer sb = new StringBuffer(ids.size()*2);
		for(int i = 0; i < ids.size(); i++) {
			sb.append("?,");
		}
		sb.deleteCharAt(sb.length()-1);
		String sql = String.format("SELECT * FROM %s WHERE %s in (%s)", tm.getTable(),tm.getPrimaryKey());
		List<T> values = jdbcTemplate.query(sql, ids.toArray(), rowMapper);
		return values;
	}
	public <T> Page<T> findByEntity(T entity, Integer page, Integer pageSize) {
		// TODO Auto-generated method stub
		return null;
	}
}
