package io.onceonly.db.dao;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import io.onceonly.db.meta.ColumnMeta;
import io.onceonly.db.meta.DDMeta;
import io.onceonly.db.meta.TableMeta;
import io.onceonly.util.OOAssert;
import io.onceonly.util.OOLog;
import io.onceonly.util.OOUtils;
import io.onceonly.util.Tuple2;

public class DaoHelper {
	private JdbcTemplate jdbcTemplate;

	private Map<String,TableMeta> tableToTableMata;
	@SuppressWarnings("rawtypes")
	private Map<String,RowMapper> tableToRowMapper = new HashMap<>();
	private Map<String,DDMeta> tableToDDMata;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public Map<String, TableMeta> getTableToTableMata() {
		return tableToTableMata;
	}

	public void setTableToTableMata(Map<String, TableMeta> tableToTableMata) {
		this.tableToTableMata = tableToTableMata;
	}

	@SuppressWarnings("rawtypes")
	public Map<String, RowMapper> getTableToRowMapper() {
		return tableToRowMapper;
	}

	@SuppressWarnings("rawtypes")
	public void setTableToRowMapper(Map<String, RowMapper> tableToRowMapper) {
		this.tableToRowMapper = tableToRowMapper;
	}

	public Map<String, DDMeta> getTableToDDMata() {
		return tableToDDMata;
	}

	public void setTableToDDMata(Map<String, DDMeta> tableToDDMata) {
		this.tableToDDMata = tableToDDMata;
	}

	public <E> boolean createOrUpdate(Class<E> tbl) {
		TableMeta old = tableToTableMata.get(tbl.getSimpleName());
		if(old == null) {
			old = TableMeta.createBy(tbl);
			List<String> sqls = old.createTableSql();
			jdbcTemplate.batchUpdate(sqls.toArray(new String[0]));
			old.freshNameToField();
			tableToTableMata.put(tbl.getSimpleName(), old);
			return true;
		}else {
			TableMeta tm = TableMeta.createBy(tbl);
			if(old.equals(tm)){
				return false;
			} else if(tm != null){
				List<String> sqls = old.upgradeTo(tm);
				System.out.println(OOUtils.toJSON(sqls));
				if(!sqls.isEmpty()){
					jdbcTemplate.batchUpdate(sqls.toArray(new String[0]));	
				}
				tm.freshNameToField();
				tableToTableMata.put(tbl.getSimpleName(), tm);
				return true;
			}
		}
		return false;
	}

	public <E> boolean drop(Class<E> tbl) {
		TableMeta tm = tableToTableMata.get(tbl.getSimpleName());
		if(tm == null) {
			return false;
		}
		String sql = String.format("DROP TABLE IF EXISTS %s;", tbl.getSimpleName());
		jdbcTemplate.batchUpdate(sql);
		return true;
	}

	public int[] batchUpdate(final String... sql) throws DataAccessException {
		return jdbcTemplate.batchUpdate(sql);
	}
	
	public int[] batchUpdate(final String sql,List<Object[]> batchArgs) throws DataAccessException {
		return jdbcTemplate.batchUpdate(sql, batchArgs);
	}
	
	private <E> RowMapper<E> genRowMapper(Class<E> tbl,TableMeta tm) {
		RowMapper<E> rowMapper = new RowMapper<E>(){
			@Override
			public E mapRow(ResultSet rs, int rowNum) throws SQLException {
				E row = null;
				if(rs.next()) {
					try {
						row = tbl.newInstance();
						List<ColumnMeta> columnMetas = tm.getColumnMetas();
						for(ColumnMeta colMeta:columnMetas) {
							Field field = colMeta.getField();
							Object val = rs.getObject(colMeta.getName(), field.getType());
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
	public <E,ID> E get(Class<E> tbl,ID id) {
		TableMeta tm = tableToTableMata.get(tbl.getSimpleName());
		if(tm == null) {
			return null;
		}
		RowMapper<E> rowMapper = null;
		if(!tableToRowMapper.containsKey(tbl.getSimpleName())) {
			rowMapper = genRowMapper(tbl,tm);
			tableToRowMapper.put(tbl.getSimpleName(), rowMapper);
		}else {
			rowMapper = tableToRowMapper.get(tbl.getSimpleName());	
		}
		String sql = String.format("SELECT * FROM %s WHERE %s = (?)", tm.getTable(),tm.getPrimaryKey());
		List<E> values = jdbcTemplate.query(sql, new Object[]{id}, rowMapper);
		if(values.size() == 1){
			return values.get(0);
		}
		return null;
	}
	
	private <E> Tuple2<List<String>,List<List<Object>>> fetchNamesValues(List<ColumnMeta> columnMetas,boolean ignoreNull,List<E> entities) {
		List<String> names = new ArrayList<>(columnMetas.size());
		List<List<Object>> valsList = new ArrayList<>(entities.size());
		boolean hasNames = false;
		for(E entity:entities) {
			if(entity == null) continue;
			List<Object> vals = new ArrayList<>(columnMetas.size());
			valsList.add(vals);
			for(ColumnMeta cm:columnMetas) {
				if(!hasNames) {
					names.add(cm.getName());	
				}
				try {
					Object val = cm.getField().get(entity);
					if(val != null || !ignoreNull) {
						vals.add(val);
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					OOAssert.warnning("%s.%s 访问异常:%s", entity.getClass().getSimpleName(),cm.getName(),e.getMessage());
				}
			}
			hasNames = true;
		}
		return new Tuple2<List<String>,List<List<Object>>>(names,valsList);
	}
	
	public <E> E insert(E entity) {
		OOAssert.warnning(entity != null,"不可以插入null");
		Class<?> tbl = entity.getClass();
		TableMeta tm = tableToTableMata.get(tbl.getSimpleName());	
		OOAssert.fatal(tm != null,"无法找到表：%s",tbl.getSimpleName());
		Tuple2<List<String>,List<List<Object>>>  nameVals = fetchNamesValues(tm.getColumnMetas(),false,Arrays.asList(entity));
		List<Object> vals = nameVals.b.get(0);
		String stub = OOUtils.genStub("?",",",nameVals.a.size());
		String sql = String.format("INSERT INTO %s(%s) VALUES(%s);", tm.getTable(),String.join(",", nameVals.a),stub);
		jdbcTemplate.update(sql, vals.toArray());
		return entity;
	}
	
	public <E> int insert(List<E> entities) {
		OOAssert.warnning(entities != null && !entities.isEmpty(),"不可以插入null");
		Class<?> tbl = entities.get(0).getClass();
		TableMeta tm = tableToTableMata.get(tbl.getSimpleName());
		OOAssert.fatal(tm != null,"无法找到表：%s",tbl.getSimpleName());
		Tuple2<List<String>,List<List<Object>>>  nameVals = fetchNamesValues(tm.getColumnMetas(),false,entities);
		String stub = OOUtils.genStub("?",",",nameVals.a.size());
		String sql = String.format("INSERT INTO %s(%s) VALUES(%s);", tm.getTable(),String.join(",", nameVals.a),stub);
		int[] cnts = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				List<Object> vals = nameVals.b.get(i);
				for(int vi = 0;  vi < vals.size(); vi++) {
					ps.setObject(vi+1, vals.get(vi));
				}
				OOLog.debug("%s values:%s",sql, OOUtils.toJSON(vals));
			}
			@Override
			public int getBatchSize() {
				return entities.size();
			}
			
		});
		int cnt = 0;
		for(int c:cnts) {
			cnt += c;
		}
		return cnt;
	}

	public <E> int update(E entity) {
		// TODO Auto-generated method stub
		return 0;
	}

	public <E> int updateIgnoreNull(E entity) {
		// TODO Auto-generated method stub
		return 0;
	}

	public <E> int updateIncrement(E increment) {
		// TODO Auto-generated method stub
		return 0;
	}

	public <E,ID> int remove(Class<E> tbl,ID id) {
		// TODO Auto-generated method stub
		return 0;
	}

	public <E,ID> int remove(Class<E> tbl, List<ID> ids) {
		// TODO Auto-generated method stub
		return 0;
	}

	public <E,ID> int delete(Class<E> tbl, ID id) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public <E> int update(E newVal, String pattern, Cnd<E> cnd) {
		// TODO Auto-generated method stub
		return 0;
	}

	public <E> int updateIncrement(E increment, Cnd<E> cnd) {
		// TODO Auto-generated method stub
		return 0;
	}

	public <E> int updateXOR(E arg, Cnd<E> cnd) {
		// TODO Auto-generated method stub
		return 0;
	}

	public <E> int remove(Class<E> tbl, Cnd<E> cnd) {
		// TODO Auto-generated method stub
		return 0;
	}

	public <E> int delete(Class<E> tbl,Cnd<E> cnd) {
		// TODO Auto-generated method stub
		return 0;
	}

	public <E> Page<E> search(Class<E> tbl,Cnd<E> cnd) {
		// TODO Auto-generated method stub
		return null;
	}

	public <E> void download(Cnd<E> cnd, Consumer<E> consumer) {
		// TODO Auto-generated method stub
		
	}

	public <E> long count(Class<E> tbl) {
		// TODO Auto-generated method stub
		return 0;
	}

	public <E> long count(Class<E> tbl, Cnd<E> cnd) {
		// TODO Auto-generated method stub
		return 0;
	}

	public <E> List<E> find(Class<E> tbl, Cnd<E> cnd) {
		return null;
	}
	@SuppressWarnings("unchecked")
	public <E,ID> List<E> findByIds(Class<E> tbl, List<ID> ids) {
		if(ids.isEmpty()) {
			return new ArrayList<E>();
		}
		TableMeta tm = tableToTableMata.get(tbl.getSimpleName());
		if(tm == null) {
		}
		RowMapper<E> rowMapper = null;
		if(!tableToRowMapper.containsKey(tbl.getSimpleName())) {
			rowMapper = genRowMapper(tbl,tm);
			tableToRowMapper.put(tbl.getSimpleName(), rowMapper);
		}else {
			rowMapper = tableToRowMapper.get(tbl.getSimpleName());	
		}
		String stub = OOUtils.genStub("?",",",ids.size());
		String sql = String.format("SELECT * FROM %s WHERE %s in (%s)", tm.getTable(),tm.getPrimaryKey(),stub);
		List<E> values = jdbcTemplate.query(sql, ids.toArray(), rowMapper);
		return values;
	}
	public <E> Page<E> findByEntity(E entity, Integer page, Integer pageSize) {
		// TODO Auto-generated method stub
		return null;
	}
}
